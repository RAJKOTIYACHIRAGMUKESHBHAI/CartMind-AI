// =========================================
// CartMind AI - API Service
// =========================================

window.CM = window.CM || {
  apiBase: "http://localhost:8080"
};


window.CMApi = {

  // =========================================
  // SEARCH PRODUCTS
  // POST /api/ai/search
  // =========================================

  async search(query) {

    const cleanQuery =
      String(query || "").trim();

    if (!cleanQuery) {
      throw new Error(
        "Please enter a search query."
      );
    }

    try {

      const response =
        await fetch(
          `${window.CM.apiBase}/api/ai/search`,
          {
            method: "POST",

            headers: {
              "Content-Type": "application/json"
            },

            body: JSON.stringify({
              query: cleanQuery
            })
          }
        );

      let data = null;

      try {
        data = await response.json();
      } catch (error) {
        data = null;
      }

      if (!response.ok) {

        throw new Error(
          data?.message ||
          `API request failed with status ${response.status}`
        );

      }

      return data;

    } catch (error) {

      console.error(
        "CartMind Search API Error:",
        error
      );

      if (error instanceof TypeError) {

        throw new Error(
          "Unable to connect to the CartMind AI server."
        );

      }

      throw error;

    }

  },


  // =========================================
  // COMPARE PRODUCTS
  // POST /api/ai/compare
  // =========================================

  async compareProducts(productIds) {

    if (
      !Array.isArray(productIds) ||
      productIds.length < 2
    ) {

      throw new Error(
        "Please select at least two products."
      );

    }

    if (productIds.length > 3) {

      throw new Error(
        "You can compare a maximum of three products."
      );

    }

    try {

      const response =
        await fetch(
          `${window.CM.apiBase}/api/ai/compare`,
          {
            method: "POST",

            headers: {
              "Content-Type": "application/json"
            },

            body: JSON.stringify({
              productIds:
                productIds.map(
                  id => Number(id)
                )
            })
          }
        );

      let data = null;

      try {
        data = await response.json();
      } catch (error) {
        data = null;
      }

      if (!response.ok) {

        throw new Error(
          data?.message ||
          `Comparison request failed with status ${response.status}`
        );

      }

      return data;

    } catch (error) {

      console.error(
        "CartMind Compare API Error:",
        error
      );

      if (error instanceof TypeError) {

        throw new Error(
          "Unable to connect to the CartMind AI server."
        );

      }

      throw error;

    }

  },


  // =========================================
  // EXPLAIN PRODUCT
  // POST /api/ai/explain
  // =========================================

  async explainProduct(
    query,
    productId
  ) {

    const cleanQuery =
      String(query || "").trim();

    const numericProductId =
      Number(productId);

    if (!cleanQuery) {

      throw new Error(
        "Search query is required."
      );

    }

    if (
      !numericProductId ||
      Number.isNaN(numericProductId)
    ) {

      throw new Error(
        "Product ID is required."
      );

    }

    try {

      const response =
        await fetch(
          `${window.CM.apiBase}/api/ai/explain`,
          {
            method: "POST",

            headers: {
              "Content-Type": "application/json"
            },

            body: JSON.stringify({
              query: cleanQuery,
              productId: numericProductId
            })
          }
        );

      const contentType =
        response.headers.get("content-type") || "";

      let data = null;

      if (contentType.includes("application/json")) {

        try {
          data = await response.json();
        } catch (error) {
          data = null;
        }

      } else {

        const text =
          await response.text();

        data =
          text && text.trim()
            ? text.trim()
            : null;

      }

      if (!response.ok) {

        throw new Error(
          (
            typeof data === "string"
              ? data
              : data?.message
          ) ||
          `Explanation request failed with status ${response.status}`
        );

      }

      return data;

    } catch (error) {

      console.error(
        "CartMind Explain API Error:",
        error
      );

      if (error instanceof TypeError) {

        throw new Error(
          "Unable to connect to the CartMind AI server."
        );

      }

      throw error;

    }

  }

};