# map-builder

`npm install && npm run build` — writes `us_states_albers.json` and copies it into
`:core:map`'s assets. `npm run preview` renders `preview.svg` for a visual check against the
site. `npm run build:adjacency` writes `adjacency.json`.

`adjacency.json` is a **build-time reference only** — it is not copied into any module's
assets. Phase 02 (`docs/ROADMAP.md` §02 "Домен") hand-ports it into the `UsState` enum's
neighbor table (`docs/ARCHITECTURE.md` §4: "Соседство штатов — таблица с сайта (adjacency.js)
без DC, внутри `UsState`"). Once that Kotlin code exists, a unit test should assert it matches
this file so the two don't drift.
