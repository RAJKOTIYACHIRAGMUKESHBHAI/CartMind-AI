// =========================================
// CartMind AI - API Service
// =========================================

const API_BASE_URL = "";

/**
 * Send search query to CartMind AI backend
 * Endpoint: POST /api/ai/search
 */
async function searchProducts(query) {
  if (!query || !query.trim()) {
    throw new Error("Please enter a search query.");
  }

  try {
    const response = await fetch(`${API_BASE_URL}/api/ai/search`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        query: query.trim()
      })
    });

    if (!response.ok) {
      throw new Error(
        `API request failed with status ${response.status}`
      );
    }

    const data = await response.json();

    return data;

  } catch (error) {
    console.error("CartMind API Error:", error);

    if (error instanceof TypeError) {
      throw new Error(
        "Unable to connect to the CartMind AI server."
      );
    }

    throw error;
  }
}