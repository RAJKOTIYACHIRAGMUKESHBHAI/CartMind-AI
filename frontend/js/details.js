// CartMind AI - Product Details Module

window.CartMindDetails = {

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

  clear() {
    sessionStorage.removeItem("cm_q");
    sessionStorage.removeItem("cm_r");
  }

};


// Details page initialization
document.addEventListener("DOMContentLoaded", () => {

  const query = CartMindDetails.getQuery();

  const queryElement = document.getElementById("detailQuery");

  if (queryElement && query) {
    queryElement.textContent = query;
  }

});