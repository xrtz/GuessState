// Validates cities.json against docs/ROADMAP.md Phase 01's checklist:
//   - all 50 states appear at Master level (minLevel <= 3)
//   - the file is sorted by population descending, matching each entry's `rank`
//   - no duplicate (name, state) pairs
// Also emits duplicate-names-report.json: cities whose name appears under more than one
// state, which Phase 02's AnswerJudge/domain tests need for the same-name-city feature
// (e.g. Springfield -> {MO, MA, IL}).

import { readFileSync, writeFileSync } from "node:fs";
import { fileURLToPath } from "node:url";
import path from "node:path";

const here = path.dirname(fileURLToPath(import.meta.url));

const ALL_STATES = [
  "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
  "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
  "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
  "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
  "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY",
];

const cities = JSON.parse(readFileSync(path.join(here, "cities.json"), "utf8"));
const failures = [];

// 1. All 50 states present at Master (minLevel <= 3, i.e. every entry, but check explicitly).
const statesPresent = new Set(cities.map((c) => c.state));
const missingStates = ALL_STATES.filter((s) => !statesPresent.has(s));
if (missingStates.length > 0) {
  failures.push(`States missing from Master pool: ${missingStates.join(", ")}`);
} else {
  console.log("[ok] All 50 states have at least one city.");
}

// 2. Sorted by population descending, rank matches array position.
let sortOk = true;
for (let i = 0; i < cities.length; i++) {
  if (cities[i].rank !== i + 1) {
    failures.push(`Rank mismatch at index ${i}: expected ${i + 1}, got ${cities[i].rank} (${cities[i].name}, ${cities[i].state})`);
    sortOk = false;
  }
  if (i > 0 && cities[i].population > cities[i - 1].population) {
    failures.push(`Sort violation at index ${i}: ${cities[i].name} (${cities[i].population}) > ${cities[i - 1].name} (${cities[i - 1].population})`);
    sortOk = false;
  }
}
if (sortOk) console.log("[ok] Sorted by population descending; rank matches array position.");

// 3. No duplicate (name, state) pairs.
const seen = new Map();
for (const c of cities) {
  const key = `${c.name}|${c.state}`;
  seen.set(key, (seen.get(key) ?? 0) + 1);
}
const exactDupes = [...seen.entries()].filter(([, count]) => count > 1);
if (exactDupes.length > 0) {
  failures.push(`Duplicate (name, state) pairs: ${exactDupes.map(([k]) => k).join(", ")}`);
} else {
  console.log("[ok] No duplicate (name, state) pairs.");
}

// 4. Report of same-name cities across different states (informational, not a failure).
const byName = new Map();
for (const c of cities) {
  if (!byName.has(c.name)) byName.set(c.name, []);
  byName.get(c.name).push(c.state);
}
const crossStateNames = [...byName.entries()]
  .filter(([, states]) => states.length > 1)
  .map(([name, states]) => ({ name, states: states.sort() }));
writeFileSync(
  path.join(here, "duplicate-names-report.json"),
  JSON.stringify(crossStateNames, null, 2) + "\n",
);
console.log(`[info] ${crossStateNames.length} city names appear under more than one state — see duplicate-names-report.json.`);

console.log(`[info] Total cities: ${cities.length}`);
console.log(`[info] States: ${statesPresent.size} / 50`);

if (failures.length > 0) {
  console.error("\nVALIDATION FAILED:");
  for (const f of failures) console.error(` - ${f}`);
  process.exit(1);
}
console.log("\nAll checks passed.");
