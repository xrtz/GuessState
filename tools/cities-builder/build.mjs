// Rebuilds cities.json from real 2020 Census figures (docs/ARCHITECTURE.md §1: the site's
// own cities.js has fabricated numbers — e.g. it lists New York at 8,740,306 when the real
// 2020 census count is 8,804,190 — and a broken sort in ~10 places).
//
// Data source: the Census Bureau's live API (api.census.gov) now requires a signup API key
// (it redirects unauthenticated requests to /data/missing_key.html) that we don't have, so
// this instead parses the wikitext of Wikipedia's "List of United States cities by
// population" — itself sourced from the 2020 census and format-stable enough to parse
// reliably (the `{{change|invert=on|<2025 estimate>|<2020 census>}}` template holds both
// figures per row). `wikitext-raw.txt` is a checked-in snapshot fetched via:
//   https://en.wikipedia.org/w/api.php?action=parse&page=List_of_United_States_cities_by_population&prop=wikitext&format=json
// It only covers incorporated places with population >= ~89,000, which comfortably covers
// the top 200 but misses each of a handful of low-population states' largest city — those
// five (DE, ME, VT, WV, WY) are supplied individually in supplement.json, each sourced from
// that city's own Wikipedia infobox (2020 census figure), with a citation note.
//
// One deliberate simplification: Honolulu is included as Hawaii's largest city even though
// it is technically a CDP with no incorporated government of its own (Hawaii's only
// municipal government is at the county level) — every general-purpose "US cities by
// population" source treats it as a city in practice (this Wikipedia table does too), and
// so did the original site (cities.js lists it at rank ~56).
//
// Unlike the site, city names here carry NO state suffix/disambiguation (no "Springfield
// (MO)"): docs/ARCHITECTURE.md §1 reverses that — "Верен любой штат, где в пуле есть город
// с таким названием" — the domain (Phase 02's AnswerJudge) resolves same-named cities by
// accepting any state whose pool contains that exact name, so the data must stay plain.

import { readFileSync, writeFileSync, mkdirSync } from "node:fs";
import { fileURLToPath } from "node:url";
import path from "node:path";

const here = path.dirname(fileURLToPath(import.meta.url));
const repoRoot = path.resolve(here, "..", "..");

const TOP_N = 200;
const LEVEL_1_CUTOFF = 50;
const LEVEL_2_CUTOFF = 100;

const ALL_STATES = [
  "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
  "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
  "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
  "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
  "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY",
];

function parseWikitext() {
  const raw = readFileSync(path.join(here, "wikitext-raw.txt"), "utf8");
  const lines = raw.split("\n");

  const startIdx = lines.findIndex((l) => l.includes("sticky-header-multi"));
  if (startIdx === -1) throw new Error("Could not locate the population table in wikitext-raw.txt");
  let endIdx = -1;
  for (let i = startIdx + 1; i < lines.length; i++) {
    if (lines[i].trim() === "|}") {
      endIdx = i;
      break;
    }
  }
  if (endIdx === -1) throw new Error("Could not find the end of the population table");

  const body = lines.slice(startIdx, endIdx).join("\n");
  const rows = body.split(/\n\|-\n/).slice(1);

  const results = [];
  for (const row of rows) {
    const cityMatch = row.match(/\[\[([^\]|]+)(?:\|([^\]]+))?\]\]/);
    const popMatch = row.match(/\{\{change\|invert=on\|-?\d+\|(\d+)\}\}/);
    const stateMatch = row.match(/\n\| \[\[[^\]|]+\|([A-Z]{2})\]\]\s*\n/);
    if (!cityMatch || !popMatch || !stateMatch) continue;
    const name = (cityMatch[2] || cityMatch[1]).split(",")[0].trim();
    const state = stateMatch[1];
    if (state === "DC") continue; // not a playable state
    results.push({ name, state, population: parseInt(popMatch[1], 10) });
  }
  return results;
}

function slugify(name, state) {
  const base = name
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/^-+|-+$/g, "");
  return `${base}-${state.toLowerCase()}`;
}

function minLevelForRank(rank) {
  if (rank <= LEVEL_1_CUTOFF) return 1;
  if (rank <= LEVEL_2_CUTOFF) return 2;
  return 3;
}

function build() {
  const parsed = parseWikitext();
  const supplement = JSON.parse(readFileSync(path.join(here, "supplement.json"), "utf8"))
    .map(({ name, state, population }) => ({ name, state, population }));

  const all = [...parsed, ...supplement];
  all.sort((a, b) => b.population - a.population);

  const natural200 = all.slice(0, TOP_N);
  const statesIn200 = new Set(natural200.map((c) => c.state));
  const missingStates = ALL_STATES.filter((s) => !statesIn200.has(s));

  const additions = [];
  for (const state of missingStates) {
    const best = all
      .filter((c) => c.state === state)
      .sort((a, b) => b.population - a.population)[0];
    if (!best) throw new Error(`No city data at all for state ${state} — need a supplement.json entry`);
    additions.push(best);
  }

  const finalSet = [...natural200, ...additions];
  finalSet.sort((a, b) => b.population - a.population);

  const cities = finalSet.map((c, i) => {
    const rank = i + 1;
    return {
      id: slugify(c.name, c.state),
      name: c.name,
      state: c.state,
      population: c.population,
      rank,
      minLevel: minLevelForRank(rank),
    };
  });

  const outPath = path.join(here, "cities.json");
  writeFileSync(outPath, JSON.stringify(cities, null, 2) + "\n");
  console.log(`Wrote ${cities.length} cities to ${outPath}`);
  console.log(`State-coverage additions beyond the natural top ${TOP_N}: ${missingStates.join(", ")}`);

  const assetDir = path.join(repoRoot, "core", "data", "src", "main", "assets");
  mkdirSync(assetDir, { recursive: true });
  writeFileSync(path.join(assetDir, "cities.json"), JSON.stringify(cities, null, 2) + "\n");
  console.log(`Copied to ${path.join(assetDir, "cities.json")}`);

  return cities;
}

build();
