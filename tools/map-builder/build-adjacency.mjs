// Ports the site's hand-curated state-adjacency table (which neighbor lookups already work
// correctly on the site) into a clean, DC-stripped reference file: adjacency.json.
// Source: C:\Users\thund\Downloads\city-quiz-v4\city-quiz\src\lib\adjacency.js (ADJACENT).
//
// docs/ROADMAP.md Phase 01 asks for this table; docs/ARCHITECTURE.md §4 says the actual
// Kotlin lives inside the `UsState` enum, built in Phase 02 ("Сущности: UsState с соседями")
// — that Kotlin code is NOT written here, this is just the verified data source for it to be
// hand-ported from. :core:domain is pure JVM with no Android assets folder, so this is a
// build-time reference artifact under tools/, not a shipped runtime asset.
//
// DC is removed entirely here: it's not a UsState in the domain (docs/ARCHITECTURE.md §1:
// "DC — только декор"), so both its own entry and its appearance in MD's/VA's neighbor
// lists (the only two states that bordered it on the site) are stripped.

import { writeFileSync } from "node:fs";
import { fileURLToPath } from "node:url";
import path from "node:path";

const here = path.dirname(fileURLToPath(import.meta.url));

// Copied verbatim from the site's adjacency.js, DC removed from MD/VA, and the DC key dropped.
const ADJACENT = {
  AL: ["FL", "GA", "MS", "TN"],
  AK: [],
  AZ: ["CA", "CO", "NM", "NV", "UT"],
  AR: ["LA", "MO", "MS", "OK", "TN", "TX"],
  CA: ["AZ", "NV", "OR"],
  CO: ["AZ", "KS", "NE", "NM", "OK", "UT", "WY"],
  CT: ["MA", "NY", "RI"],
  DE: ["MD", "NJ", "PA"],
  FL: ["AL", "GA"],
  GA: ["AL", "FL", "NC", "SC", "TN"],
  HI: [],
  ID: ["MT", "NV", "OR", "UT", "WA", "WY"],
  IL: ["IN", "IA", "KY", "MO", "WI"],
  IN: ["IL", "KY", "MI", "OH"],
  IA: ["IL", "MN", "MO", "NE", "SD", "WI"],
  KS: ["CO", "MO", "NE", "OK"],
  KY: ["IL", "IN", "MO", "OH", "TN", "VA", "WV"],
  LA: ["AR", "MS", "TX"],
  ME: ["NH"],
  MD: ["DE", "PA", "VA", "WV"],
  MA: ["CT", "NH", "NY", "RI", "VT"],
  MI: ["IN", "OH", "WI"],
  MN: ["IA", "ND", "SD", "WI"],
  MS: ["AL", "AR", "LA", "TN"],
  MO: ["AR", "IL", "IA", "KS", "KY", "NE", "OK", "TN"],
  MT: ["ID", "ND", "SD", "WY"],
  NE: ["CO", "IA", "KS", "MO", "SD", "WY"],
  NV: ["AZ", "CA", "ID", "OR", "UT"],
  NH: ["MA", "ME", "VT"],
  NJ: ["DE", "NY", "PA"],
  NM: ["AZ", "CO", "OK", "TX", "UT"],
  NY: ["CT", "MA", "NJ", "PA", "VT"],
  NC: ["GA", "SC", "TN", "VA"],
  ND: ["MN", "MT", "SD"],
  OH: ["IN", "KY", "MI", "PA", "WV"],
  OK: ["AR", "CO", "KS", "MO", "NM", "TX"],
  OR: ["CA", "ID", "NV", "WA"],
  PA: ["DE", "MD", "NJ", "NY", "OH", "WV"],
  RI: ["CT", "MA"],
  SC: ["GA", "NC"],
  SD: ["IA", "MN", "MT", "ND", "NE", "WY"],
  TN: ["AL", "AR", "GA", "KY", "MO", "MS", "NC", "VA"],
  TX: ["AR", "LA", "NM", "OK"],
  UT: ["AZ", "CO", "ID", "NV", "NM", "WY"],
  VT: ["MA", "NH", "NY"],
  VA: ["KY", "MD", "NC", "TN", "WV"],
  WA: ["ID", "OR"],
  WV: ["KY", "MD", "OH", "PA", "VA"],
  WI: ["IA", "IL", "MI", "MN"],
  WY: ["CO", "ID", "MT", "NE", "SD", "UT"],
};

// Sanity check: adjacency must be symmetric (A neighbors B <=> B neighbors A).
const failures = [];
for (const [state, neighbors] of Object.entries(ADJACENT)) {
  for (const n of neighbors) {
    if (!ADJACENT[n]?.includes(state)) {
      failures.push(`${state} lists ${n} as a neighbor, but ${n} does not list ${state} back`);
    }
  }
}
if (failures.length > 0) {
  console.error("Asymmetric adjacency entries found:");
  failures.forEach((f) => console.error(` - ${f}`));
  process.exit(1);
}
console.log(`[ok] Adjacency table is symmetric across all ${Object.keys(ADJACENT).length} states.`);
console.log("[ok] DC removed from the table and from MD/VA neighbor lists.");

writeFileSync(path.join(here, "adjacency.json"), JSON.stringify(ADJACENT, null, 2) + "\n");
console.log(`Wrote ${path.join(here, "adjacency.json")}`);
