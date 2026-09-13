const API_BASE_URL = "http://localhost:8080";

async function searchProducts(query) {

    const response = await fetch(`${API_BASE_URL}/api/ai/search`, {
        method: "POST",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify({
            query: query
        })
    });

    if (!response.ok) {
        throw new Error(`API request failed: ${response.status}`);
    }

    return await response.json();
}