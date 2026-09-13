document.addEventListener("DOMContentLoaded", () => {

    const form = document.getElementById("searchForm");
    const input = document.getElementById("searchInput");
    const button = document.getElementById("searchButton");

    const loadingState = document.getElementById("loadingState");
    const errorState = document.getElementById("errorState");

    const examples = document.querySelectorAll(".example-query");

    function showLoading() {
        loadingState.classList.remove("hidden");
        errorState.classList.add("hidden");

        button.disabled = true;
        button.textContent = "Searching...";
    }

    function hideLoading() {
        loadingState.classList.add("hidden");

        button.disabled = false;
        button.textContent = "Search";
    }

    function showError(message) {
        errorState.textContent = message;
        errorState.classList.remove("hidden");
    }

    form.addEventListener("submit", async (event) => {

        event.preventDefault();

        const query = input.value.trim();

        if (!query) {
            showError("Please enter what you are looking for.");
            return;
        }

        showLoading();

        try {

            const result = await searchProducts(query);

            console.log("CartMind API response:", result);

        } catch (error) {

            console.error("CartMind error:", error);

            showError(
                "Backend is not connected yet. The frontend search interface is working."
            );

        } finally {

            hideLoading();

        }
    });

    examples.forEach((example) => {

        example.addEventListener("click", () => {

            input.value = example.textContent.trim();
            input.focus();

            errorState.classList.add("hidden");

        });

    });

});