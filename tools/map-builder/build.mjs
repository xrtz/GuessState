// Rebuilds us_states_albers.json from us-atlas's states-10m.json (docs/ARCHITECTURE.md §6):
// TopoJSON -> topojson-client feature extraction -> d3-geo geoAlbersUsa().fitSize(...) — the
// same projection react-simple-maps uses at runtime on the site (see the site's own
// USMap.jsx, projection="geoAlbersUsa"). The 800x600 canvas size matches the site's
// ComposableMap viewBox exactly (see city-quiz's App.css: "ComposableMap viewBox is 800x600
// (4:3)"), so the projected coordinates here line up with what the site renders.
//
// Territories (PR, GU, VI, AS, MP) are dropped — not on the game's map. DC is kept: it's
// drawn for visual completeness (docs/ARCHITECTURE.md §1: "DC — только декор"), same as on
// the site, just not part of the playable state set.

import { readFileSync, writeFileSync, mkdirSync } from "node:fs";
import { fileURLToPath } from "node:url";
import path from "node:path";
import { feature } from "topojson-client";
import { geoAlbersUsa, geoPath } from "d3-geo";
import { FIPS_TO_USPS, TERRITORY_FIPS } from "./fips.mjs";

const here = path.dirname(fileURLToPath(import.meta.url));
const repoRoot = path.resolve(here, "..", "..");

const WIDTH = 800;
const HEIGHT = 600;
const ROUND = (n) => Math.round(n * 10) / 10;

function main() {
  const topology = JSON.parse(
    readFileSync(path.join(here, "node_modules", "us-atlas", "states-10m.json"), "utf8"),
  );
  const allStates = feature(topology, topology.objects.states);

  const playable = {
    ...allStates,
    features: allStates.features.filter((f) => !TERRITORY_FIPS.has(String(f.id))),
  };

  const projection = geoAlbersUsa().fitSize([WIDTH, HEIGHT], playable);
  const path2 = geoPath(projection);

  const states = playable.features.map((f) => {
    const fips = String(f.id).padStart(2, "0");
    const code = FIPS_TO_USPS[fips];
    if (!code) throw new Error(`No USPS code for FIPS ${fips} (${f.properties?.name})`);

    const geom = f.geometry;
    const polygons = geom.type === "Polygon" ? [geom.coordinates] : geom.coordinates;
    const rings = polygons.map((polygon) =>
      polygon.map((ring) =>
        ring.map(([lon, lat]) => {
          const [x, y] = projection([lon, lat]);
          return [ROUND(x), ROUND(y)];
        }),
      ),
    );

    const allPoints = rings.flat(2);
    const xs = allPoints.map((p) => p[0]);
    const ys = allPoints.map((p) => p[1]);
    const bbox = [ROUND(Math.min(...xs)), ROUND(Math.min(...ys)), ROUND(Math.max(...xs)), ROUND(Math.max(...ys))];

    const [cx, cy] = path2.centroid(f);
    const label = [ROUND(cx), ROUND(cy)];

    return {
      code,
      name: f.properties?.name ?? code,
      rings,
      bbox,
      label,
    };
  });

  states.sort((a, b) => a.code.localeCompare(b.code));

  const output = { width: WIDTH, height: HEIGHT, states };

  const outPath = path.join(here, "us_states_albers.json");
  writeFileSync(outPath, JSON.stringify(output) + "\n");
  console.log(`Wrote ${states.length} states to ${outPath}`);

  const assetDir = path.join(repoRoot, "core", "map", "src", "main", "assets");
  mkdirSync(assetDir, { recursive: true });
  writeFileSync(path.join(assetDir, "us_states_albers.json"), JSON.stringify(output) + "\n");
  console.log(`Copied to ${path.join(assetDir, "us_states_albers.json")}`);

  return output;
}

main();
