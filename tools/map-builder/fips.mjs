// FIPS state code -> USPS 2-letter code. Copied from the site's own table (the source of
// truth we're matching): C:\Users\thund\Downloads\city-quiz-v4\city-quiz\src\components\USMap.jsx
// DC (11) is included here (unlike the site's FIPS table, which omits it so clicks on that
// tiny polygon are silently ignored) because this asset is a geometry dataset, not a hit-test
// table — :core:map keeps DC's shape for visual completeness (docs/ARCHITECTURE.md §1: "DC —
// только декор"), and Phase 05 decides whether it's drawn/interactive.
export const FIPS_TO_USPS = {
  "01": "AL", "02": "AK", "04": "AZ", "05": "AR", "06": "CA",
  "08": "CO", "09": "CT", "10": "DE", "11": "DC", "12": "FL",
  "13": "GA", "15": "HI", "16": "ID", "17": "IL", "18": "IN",
  "19": "IA", "20": "KS", "21": "KY", "22": "LA", "23": "ME",
  "24": "MD", "25": "MA", "26": "MI", "27": "MN", "28": "MS",
  "29": "MO", "30": "MT", "31": "NE", "32": "NV", "33": "NH",
  "34": "NJ", "35": "NM", "36": "NY", "37": "NC", "38": "ND",
  "39": "OH", "40": "OK", "41": "OR", "42": "PA", "44": "RI",
  "45": "SC", "46": "SD", "47": "TN", "48": "TX", "49": "UT",
  "50": "VT", "51": "VA", "53": "WA", "54": "WV", "55": "WI",
  "56": "WY",
};

// Territories present in us-atlas/states-10m.json that aren't on the game's map.
export const TERRITORY_FIPS = new Set(["60", "66", "69", "72", "78"]);
