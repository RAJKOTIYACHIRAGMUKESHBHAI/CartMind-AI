// CartMind AI - Filters Module

window.CartMindFilters = {

  getValue(element) {
    if (!element) return "";

    return String(element.value || "").trim();
  },

  apply(items, filters = {}) {
    if (!Array.isArray(items)) return [];

    return items.filter((item) => {

      if (!item || typeof item !== "object") {
        return false;
      }

      return Object.keys(filters).every((key) => {

        const filterValue = String(filters[key] ?? "").trim();

        if (!filterValue) {
          return true;
        }

        const itemValue = item[key];

        if (itemValue === undefined || itemValue === null) {
          return false;
        }

        return String(itemValue)
          .toLowerCase()
          .includes(filterValue.toLowerCase());

      });

    });
  },

  clear(form) {
    if (!form) return;

    if (typeof form.reset === "function") {
      form.reset();
    }
  }

};