// Renders us_states_albers.json straight to SVG (no headless browser) so the projection can
// be eyeballed against the site's rendering of the same data (react-simple-maps + the same
// geoAlbersUsa projection). Fill/stroke colors match the site's USMap.jsx C.fill/C.stroke.

import { readFileSync, writeFileSync } from "node:fs";
import { fileURLToPath } from "node:url";
import path from "node:path";

const here = path.dirname(fileURLToPath(import.meta.url));
const data = JSON.parse(readFileSync(path.join(here, "us_states_albers.json"), "utf8"));

function ringToPath(ring) {
  return "M" + ring.map(([x, y]) => `${x},${y}`).join("L") + "Z";
}

const paths = data.states
  .map((s) => {
    const d = s.rings.map((polygon) => polygon.map(ringToPath).join(" ")).join(" ");
    const fill = s.code === "DC" ? "#c9c5b5" : "#E2DFD4";
    return `<path d="${d}" fill="${fill}" stroke="rgba(22,32,63,0.22)" stroke-width="0.5" />` +
      `<text x="${s.label[0]}" y="${s.label[1]}" font-size="7" text-anchor="middle" fill="#16203F" font-family="monospace">${s.code}</text>`;
  })
  .join("\n");

const svg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ${data.width} ${data.height}" width="${data.width}" height="${data.height}">
<rect width="${data.width}" height="${data.height}" fill="#F3F1EA" />
${paths}
</svg>
`;

writeFileSync(path.join(here, "preview.svg"), svg);
console.log(`Wrote ${path.join(here, "preview.svg")}`);
