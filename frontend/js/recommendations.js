// CartMind AI - Recommendations Module

window.CartMindRecommendations = {

  save(query, response) {
    try {
      sessionStorage.setItem("cm_recommendation_query", query || "");
      sessionStorage.setItem(
        "cm_recommendation_response",
        JSON.stringify(response ?? null)
      );
    } catch (error) {
      console.warn("Could not store recommendation data.");
    }
  },

  getQuery() {
    return sessionStorage.getItem("cm_recommendation_query") || "";
  },

  getResponse() {
    const raw = sessionStorage.getItem("cm_recommendation_response");

    if (!raw) return null;

    try {
      return JSON.parse(raw);
    } catch (error) {
      console.warn("Invalid recommendation response.");
      return null;
    }
  },

  clear() {
    sessionStorage.removeItem("cm_recommendation_query");
    sessionStorage.removeItem("cm_recommendation_response");
  }

};