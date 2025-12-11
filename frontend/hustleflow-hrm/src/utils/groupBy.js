// src/utils/groupBy.js
export function groupBy(array = [], key) {
  return array.reduce((acc, item) => {
    const k = item && (item[key] ?? 'Unknown');
    if (!acc[k]) acc[k] = [];
    acc[k].push(item);
    return acc;
  }, {});
}
