// CartMind AI - Products Module

window.CartMindProducts = {

  getQuery() {
    return sessionStorage.getItem("cm_q") || "";
  },

  getResponse() {
    const raw = sessionStorage.getItem("cm_r");

    if (!raw) return null;

    try {
      return JSON.parse(raw);
    } catch (error) {
      console.warn("Invalid stored API response.");
      return null;
    }
  },

  clearResults() {
    const container =
      document.getElementById("productsGrid") ||
      document.getElementById("productResults");

    if (container) {
      container.innerHTML = "";
    }
  },

  setSearchValue() {
    const input = document.getElementById("productsSearchInput");
    const query = this.getQuery();

    if (input && query) {
      input.value = query;
    }
  }

};


// Products page initialization
document.addEventListener("DOMContentLoaded", () => {

  CartMindProducts.setSearchValue();

});