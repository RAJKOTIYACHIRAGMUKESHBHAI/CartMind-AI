// CartMind AI - Search Module

window.CartMindSearch = {

  async run(query) {
    const cleanQuery = String(query || "").trim();

    if (!cleanQuery) {
      throw new Error("Please enter a search query.");
    }

    if (
      !window.CartMindAPI ||
      typeof CartMindAPI.search !== "function"
    ) {
      throw new Error("Search service is not available.");
    }

    const response = await CartMindAPI.search(cleanQuery);

    try {
      sessionStorage.setItem("cm_q", cleanQuery);
      sessionStorage.setItem("cm_r", JSON.stringify(response));
    } catch (error) {
      console.warn("Could not save search response.");
    }

    return response;
  },

  getLastQuery() {
    return sessionStorage.getItem("cm_q") || "";
  },

  getLastResponse() {
    const raw = sessionStorage.getItem("cm_r");

    if (!raw) return null;

    try {
      return JSON.parse(raw);
    } catch (error) {
      console.warn("Invalid saved search response.");
      return null;
    }
  },

  clear() {
    sessionStorage.removeItem("cm_q");
    sessionStorage.removeItem("cm_r");
  }

};