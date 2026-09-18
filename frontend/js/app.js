window.CM = window.CM || {
  apiBase: ""
};

window.CMApi = {

  async search(query) {

    const cleanQuery = String(query || "").trim();

    if (!cleanQuery) {
      throw new Error("Please enter a search query.");
    }

    let response;

    try {

      response = await fetch(
        (window.CM.apiBase || "") + "/api/ai/search",
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

    } catch (error) {

      throw new Error(
        "Unable to connect to CartMind AI server."
      );

    }

    let data = null;

    try {
      data = await response.json();
    } catch (error) {
      data = null;
    }

    if (!response.ok) {

      throw new Error(
        data?.message || "AI search request failed."
      );

    }

    return data;
  }

};


document.addEventListener("DOMContentLoaded", () => {

  /* =========================
     NAVIGATION MENU
  ========================= */

  document
    .querySelectorAll(".menu")
    .forEach(button => {

      button.addEventListener("click", () => {

        const mobileMenu =
          document.querySelector(".mobile-menu");

        if (mobileMenu) {
          mobileMenu.classList.toggle("open");
        }

      });

    });


  /* =========================
     HOME QUICK SEARCH BUTTONS
  ========================= */

  document
    .querySelectorAll("[data-query]")
    .forEach(button => {

      button.addEventListener("click", () => {

        const input =
          document.querySelector("#searchInput");

        if (!input) return;

        input.value = button.dataset.query;
        input.focus();

      });

    });


  /* =========================
     HOME SEARCH
  ========================= */

  const homeForm =
    document.querySelector("#searchForm");

  if (homeForm) {

    homeForm.addEventListener("submit", async event => {

      event.preventDefault();

      const input =
        document.querySelector("#searchInput");

      const query =
        input?.value.trim();

      if (!query) return;

      const button =
        homeForm.querySelector("button");

      const originalText =
        button?.textContent;

      try {

        if (button) {
          button.disabled = true;
          button.textContent = "Thinking...";
        }

        sessionStorage.setItem(
          "cm_q",
          query
        );

        await CMApi.search(query);

        window.location.href =
          "products.html";

      } catch (error) {

        alert(
          error.message ||
          "Something went wrong."
        );

      } finally {

        if (button) {
          button.disabled = false;
          button.textContent =
            originalText || "Search with AI →";
        }

      }

    });

  }


  /* =========================
     PRODUCTS SEARCH
  ========================= */

  const productsForm =
    document.querySelector(
      "#productsSearchForm"
    );

  if (productsForm) {

    productsForm.addEventListener(
      "submit",
      async event => {

        event.preventDefault();

        const input =
          document.querySelector(
            "#productsSearchInput"
          );

        const query =
          input?.value.trim();

        if (!query) return;

        const button =
          productsForm.querySelector("button");

        const originalText =
          button?.textContent;

        try {

          if (button) {
            button.disabled = true;
            button.textContent = "Searching...";
          }

          const data =
            await CMApi.search(query);

          sessionStorage.setItem(
            "cm_q",
            query
          );

          sessionStorage.setItem(
            "cm_r",
            JSON.stringify(data)
          );

          const emptyState =
            document.querySelector(
              "#productsEmpty"
            );

          if (emptyState) {
            emptyState.remove();
          }

          const grid =
            document.querySelector(
              "#productsGrid"
            );

          if (grid) {

            grid.innerHTML = `
              <article class="card product">
                <div class="product-art">CM</div>

                <span class="eyebrow">
                  AI RESPONSE
                </span>

                <h3>
                  API response received
                </h3>

                <p>
                  Your backend response has been
                  received successfully. Map the
                  documented response fields here
                  for live product cards.
                </p>
              </article>
            `;

          }

        } catch (error) {

          alert(
            error.message ||
            "Product search failed."
          );

        } finally {

          if (button) {
            button.disabled = false;
            button.textContent =
              originalText || "Search";
          }

        }

      }
    );

  }


  /* =========================
     COMPARE
  ========================= */

  const compareButton =
    document.querySelector(
      "#compareButton"
    );

  if (compareButton) {

    compareButton.addEventListener(
      "click",
      () => {

        const optionA =
          document.querySelector(
            "#compareA"
          )?.value.trim();

        const optionB =
          document.querySelector(
            "#compareB"
          )?.value.trim();

        if (!optionA || !optionB) {

          alert(
            "Enter both options."
          );

          return;
        }

        const headA =
          document.querySelector(
            "#headA"
          );

        const headB =
          document.querySelector(
            "#headB"
          );

        if (headA) {
          headA.textContent =
            optionA;
        }

        if (headB) {
          headB.textContent =
            optionB;
        }

        const workspace =
          document.querySelector(
            "#workspace"
          );

        if (workspace) {

          workspace.classList.remove(
            "hidden"
          );

          workspace.style.display =
            "grid";

        }

      }
    );

  }


  /* =========================
     AI ASSISTANT
  ========================= */

  const assistantForm =
    document.querySelector(
      "#assistantForm"
    );

  if (assistantForm) {

    assistantForm.addEventListener(
      "submit",
      async event => {

        event.preventDefault();

        const input =
          document.querySelector(
            "#assistantInput"
          );

        const query =
          input?.value.trim();

        if (!query) return;

        const messages =
          document.querySelector(
            "#messages"
          );

        if (!messages) return;

        const userMessage =
          document.createElement("div");

        userMessage.className =
          "msg user";

        userMessage.textContent =
          query;

        messages.appendChild(
          userMessage
        );

        input.value = "";

        const button =
          assistantForm.querySelector(
            "button"
          );

        const originalText =
          button?.textContent;

        try {

          if (button) {
            button.disabled = true;
            button.textContent = "Thinking...";
          }

          await CMApi.search(query);

          const aiMessage =
            document.createElement("div");

          aiMessage.className =
            "msg ai";

          aiMessage.textContent =
            "Request sent to CartMind AI. Connect the documented response fields here.";

          messages.appendChild(
            aiMessage
          );

          messages.scrollTop =
            messages.scrollHeight;

        } catch (error) {

          const errorMessage =
            document.createElement("div");

          errorMessage.className =
            "msg ai";

          errorMessage.textContent =
            error.message ||
            "Unable to process the request.";

          messages.appendChild(
            errorMessage
          );

        } finally {

          if (button) {
            button.disabled = false;
            button.textContent =
              originalText || "Send →";
          }

        }

      }
    );

  }


  /* =========================
     REGISTER
  ========================= */

  const registerForm =
    document.querySelector(
      "#registerForm"
    );

  if (registerForm) {

    const password =
      document.querySelector(
        "#registerPassword"
      );

    if (password) {

      password.addEventListener(
        "input",
        () => {

          const value =
            password.value;

          const rules = {

            length:
              value.length >= 8,

            upper:
              /[A-Z]/.test(value),

            lower:
              /[a-z]/.test(value),

            number:
              /[0-9]/.test(value)

          };

          document
            .querySelectorAll(
              "[data-rule]"
            )
            .forEach(rule => {

              rule.classList.toggle(
                "ok",
                Boolean(
                  rules[
                    rule.dataset.rule
                  ]
                )
              );

            });

        }
      );

    }


    registerForm.addEventListener(
      "submit",
      event => {

        event.preventDefault();

        const name =
          document.querySelector(
            "#registerName"
          )?.value.trim();

        const email =
          document.querySelector(
            "#registerEmail"
          )?.value.trim();

        const passwordValue =
          document.querySelector(
            "#registerPassword"
          )?.value;

        const confirmPassword =
          document.querySelector(
            "#registerConfirm"
          )?.value;

        const terms =
          document.querySelector(
            "#registerTerms"
          )?.checked;


        const validEmail =
          CartMindValidation.email(
            email
          );

        const validPassword =
          CartMindValidation.password(
            passwordValue
          );

        const valid =
          Boolean(
            name &&
            name.length >= 2 &&
            validEmail &&
            validPassword &&
            passwordValue === confirmPassword &&
            terms
          );


        const message =
          document.querySelector(
            "#registerMessage"
          );


        if (valid) {

          CartMindUI.showMessage(
            message,
            "Registration details validated. Connect your backend registration endpoint.",
            "okmsg"
          );

        } else {

          CartMindUI.showMessage(
            message,
            "Please complete all registration validations.",
            "errmsg"
          );

        }

      }
    );

  }


  /* =========================
     LOGIN
  ========================= */

  const loginForm =
    document.querySelector(
      "#loginForm"
    );

  if (loginForm) {

    loginForm.addEventListener(
      "submit",
      event => {

        event.preventDefault();

        const email =
          document.querySelector(
            "#loginEmail"
          )?.value.trim();

        const password =
          document.querySelector(
            "#loginPassword"
          )?.value;

        const validEmail =
          CartMindValidation.email(
            email
          );

        const valid =
          validEmail &&
          password.length >= 8;

        const message =
          document.querySelector(
            "#loginMessage"
          );


        if (valid) {

          CartMindUI.showMessage(
            message,
            "Login details validated. Connect your backend authentication endpoint.",
            "okmsg"
          );

        } else {

          CartMindUI.showMessage(
            message,
            "Enter a valid email and 8+ character password.",
            "errmsg"
          );

        }

      }
    );

  }

});