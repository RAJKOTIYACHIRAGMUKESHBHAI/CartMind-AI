window.CM = window.CM || {
  apiBase: "http://localhost:8080"
};

/* =========================================================
   HELPER FUNCTIONS
========================================================= */

function escapeHtml(value) {

  return String(value ?? "")
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");

}


/* =========================================================
   FORMAT PRODUCT ATTRIBUTES
========================================================= */

function formatAttributes(attributes) {

  if (!attributes) {

    return "";

  }


  if (
    typeof attributes === "string"
  ) {

    try {

      const parsed =
        JSON.parse(attributes);


      if (
        parsed &&
        typeof parsed === "object" &&
        !Array.isArray(parsed)
      ) {

        attributes = parsed;

      }

    } catch (error) {

      return escapeHtml(
        attributes
      );

    }

  }


  if (
    typeof attributes === "object" &&
    !Array.isArray(attributes)
  ) {

    return Object.entries(attributes)
      .map(
        ([key, value]) => {

          return `
            <div class="spec-row">

              <span>
                ${escapeHtml(key)}
              </span>

              <strong>
                ${escapeHtml(value)}
              </strong>

            </div>
          `;

        }
      )
      .join("");

  }


  return escapeHtml(
    attributes
  );

}


/* =========================================================
   FORMAT PRICE
========================================================= */

function formatPrice(price) {

  if (
    price === null ||
    price === undefined ||
    price === ""
  ) {

    return "Price unavailable";

  }


  const numericPrice =
    Number(price);


  if (Number.isNaN(numericPrice)) {

    return "Price unavailable";

  }


  return new Intl.NumberFormat(
    "en-IN",
    {
      style: "currency",
      currency: "INR",
      maximumFractionDigits: 0
    }
  ).format(numericPrice);

}


/* =========================================================
   RENDER PRODUCTS
========================================================= */

function renderProducts(data) {

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


  if (!grid) {

    return;

  }


  /*
   * IMPORTANT:
   * Normal products array contains the real DB id.
   * Ranked products may not contain id.
   * We use externalProductId to restore the id.
   */

  const normalProducts =
    Array.isArray(data?.products)
      ? data.products
      : [];


  const items =
    data?.rankedProducts?.length
      ? data.rankedProducts
      : normalProducts.map(
          product => ({
            product
          })
        );


  if (!items.length) {

    grid.innerHTML = `

      <article class="card product">

        <div class="product-art">
          CM
        </div>

        <span class="eyebrow">
          NO RESULTS
        </span>

        <h3>
          No matching products found
        </h3>

        <p>
          Try changing your budget,
          category, or requirements.
        </p>

      </article>

    `;

    return;

  }


  grid.innerHTML =
    items
      .map(item => {

        /*
         * Get ranked product
         */

        const rankedProduct =
          item?.product || item;


        /*
         * Find original product record
         * so we can restore the database id.
         */

        const originalProduct =
          normalProducts.find(
            product =>
              product?.externalProductId &&
              product.externalProductId ===
                rankedProduct?.externalProductId
          );


        /*
         * Create final product object
         * with the correct id.
         */

        const p = {

          ...rankedProduct,

          id:
            rankedProduct?.id ??
            originalProduct?.id

        };


        const score =
          item?.scoreBreakdown?.totalScore;


        const isLive =
          p?.live === true;


        const rating =
          p?.rating !== null &&
          p?.rating !== undefined
            ? `★ ${p.rating}`
            : "Rating unavailable";


        const source =
          p?.provider ||
          "Unknown source";


        const sourceLabel =
          isLive
            ? `LIVE · ${source}`
            : `DEMO · ${source}`;


        const image =
          p?.imageUrl
            ? `
              <img
                src="${escapeHtml(
                  p.imageUrl
                )}"
                alt="${escapeHtml(
                  p.name ||
                  "Product"
                )}"
              >
            `
            : `
              <div class="product-art">
                CM
              </div>
            `;


        /*
         * Original provider link
         */

        const productLink =
          p?.productUrl
            ? `
              <a
                class="btn btn-primary"
                href="${escapeHtml(
                  p.productUrl
                )}"
                target="_blank"
                rel="noopener noreferrer"
              >
                View Product ↗
              </a>
            `
            : `
              <span
                class="btn btn-primary"
                aria-disabled="true"
              >
                Demo Product
              </span>
            `;


        /*
         * Details page link
         */

        const detailsLink =
          p?.id
            ? `
              <a
                class="btn"
                href="details.html?id=${encodeURIComponent(
                  p.id
                )}"
              >
                View Details
              </a>
            `
            : "";


        const specs =
          formatAttributes(
            p?.attributes
          );


        return `

          <article class="card product">

            <div class="product-media">

              ${image}

            </div>


            <div class="product-body">

              <span class="eyebrow">

                ${escapeHtml(
                  sourceLabel
                )}

              </span>


              <h3>

                ${escapeHtml(
                  p?.name ||
                  "Unnamed Product"
                )}

              </h3>


              <p>

                ${
                  p?.description
                    ? escapeHtml(
                        p.description
                      )
                    : "No description available."
                }

              </p>


              <div class="product-meta">

                <strong>

                  ${formatPrice(
                    p?.price
                  )}

                </strong>


                <span>

                  ${escapeHtml(
                    rating
                  )}

                </span>

              </div>


              ${
                score !== undefined &&
                score !== null
                  ? `
                    <div class="product-score">

                      CartMind Score:

                      <strong>

                        ${Math.round(
                          score
                        )}/100

                      </strong>

                    </div>
                  `
                  : ""
              }


              ${
                specs
                  ? `
                    <div class="product-specs">

                      ${specs}

                    </div>
                  `
                  : ""
              }


              <div class="product-actions">

                ${detailsLink}

                ${productLink}

              </div>


            </div>

          </article>

        `;

      })
      .join("");

}


/* =========================================================
   RENDER RECOMMENDATIONS
========================================================= */

function renderRecommendations(data) {

  const section =
    document.querySelector(
      "#recommendationsSection"
    );

  const grid =
    document.querySelector(
      "#recommendationsGrid"
    );

  if (!section || !grid) {
    return;
  }

  const recommendations =
    data?.recommendations || {};

  const recList = Object.entries(
    recommendations
  ).filter(
    ([key, value]) =>
      value &&
      value.product
  );

  if (!recList.length) {
    section.style.display = "none";
    return;
  }

  section.style.display = "block";

  grid.innerHTML =
    recList
      .map(
        ([recType, item]) => {

          const p = item.product;

          const recLabel =
            recType
              .replace(/_/g, " ")
              .replace(/\b\w/g, (c) =>
                c.toUpperCase()
              );

          const score =
            item?.scoreBreakdown?.totalScore;

          const isLive =
            p?.live === true;

          const rating =
            p?.rating !== null &&
            p?.rating !== undefined
              ? `★ ${p.rating}`
              : "Rating unavailable";

          const source =
            p?.provider ||
            "Unknown source";

          const sourceLabel =
            isLive
              ? `LIVE · ${source}`
              : `DEMO · ${source}`;

          return `
            <article class="card product">

              <div class="product-media">

                ${
                  p?.imageUrl
                    ? `
                      <img
                        src="${escapeHtml(
                          p.imageUrl
                        )}"
                        alt="${escapeHtml(
                          p.name ||
                          "Product"
                        )}"
                      >
                    `
                    : `
                      <div class="product-art">
                        CM
                      </div>
                    `
                }

              </div>

              <div class="product-body">

                <span class="eyebrow">

                  ${escapeHtml(
                    recLabel
                  )} · ${escapeHtml(
                    sourceLabel
                  )}

                </span>

                <h3>

                  ${escapeHtml(
                    p?.name ||
                    "Unnamed Product"
                  )}

                </h3>

                <div class="product-meta">

                  <strong>

                    ${formatPrice(p?.price)}

                  </strong>

                  <span>

                    ${escapeHtml(rating)}

                  </span>

                </div>

                ${
                  score !== undefined &&
                  score !== null
                    ? `
                      <div class="product-score">

                        CartMind Score:

                        <strong>

                          ${Math.round(
                            score
                          )}/100

                        </strong>

                      </div>
                    `
                    : ""
                }

              </div>

            </article>
          `;

        }
      )
      .join("");

}


/* =========================================================
   COMPARE PAGE HELPERS
========================================================= */

function getSavedProducts() {

  const savedResults =
    localStorage.getItem("cm_r");

  if (!savedResults) {
    return [];
  }

  try {

    const data =
      JSON.parse(savedResults);

    /*
     * IMPORTANT:
     * Backend response ke `products` array
     * me actual database IDs present hain.
     *
     * `rankedProducts` ke product objects me
     * id nahi hota, isliye compare ke liye
     * direct `products` array use kar rahe hain.
     */

    const products =
      Array.isArray(data?.products)
        ? data.products
        : [];

    return products.filter(
      product =>
        product &&
        product.id
    );

  } catch (error) {

    console.error(
      "Saved product data error:",
      error
    );

    return [];
  }

}


/* =========================================================
   FILL COMPARE DROPDOWNS
========================================================= */

function loadCompareProducts() {

  const compareA =
    document.querySelector("#compareA");

  const compareB =
    document.querySelector("#compareB");

  if (!compareA || !compareB) {
    return [];
  }


  const products =
    getSavedProducts();


  const empty =
    document.querySelector("#compareEmpty");


  const setup =
    document.querySelector("#compareSetup");


  if (!products.length) {

    if (empty) {
      empty.style.display = "block";
    }

    if (setup) {
      setup.style.display = "none";
    }

    return [];

  }


  /*
   * Products exist.
   * Hide the "No Search Data" message.
   */

  if (empty) {
    empty.style.display = "none";
  }


  if (setup) {
    setup.style.display = "block";
  }


  /*
   * Prevent duplicate options
   * if the function runs again.
   */

  compareA.innerHTML = `
    <option value="">
      Select first product
    </option>
  `;

  compareB.innerHTML = `
    <option value="">
      Select second product
    </option>
  `;


  products.forEach(product => {

    if (!product?.id) {
      return;
    }


    const optionA =
      document.createElement("option");

    optionA.value =
      product.id;

    optionA.textContent =
      product.name ||
      `Product ${product.id}`;


    compareA.appendChild(
      optionA
    );


    const optionB =
      document.createElement("option");

    optionB.value =
      product.id;

    optionB.textContent =
      product.name ||
      `Product ${product.id}`;


    compareB.appendChild(
      optionB
    );

  });


  return products;
}


/* =========================================================
   UPDATE COMPARE PREVIEW
========================================================= */

function updateComparePreview(
  product,
  suffix
) {

  if (!product) {

    return;

  }


  const card =
    document.querySelector(
      `#card${suffix}, #compareCard${suffix}`
    );


  if (card) {

    card.classList.add(
      "selected"
    );

  }


  const image =
    document.querySelector(
      `#image${suffix}, #compareImage${suffix}`
    );


  const source =
    document.querySelector(
      `#source${suffix}, #compareSource${suffix}`
    );


  const name =
    document.querySelector(
      `#name${suffix}, #compareName${suffix}`
    );


  const price =
    document.querySelector(
      `#price${suffix}, #comparePrice${suffix}`
    );


  const rating =
    document.querySelector(
      `#rating${suffix}, #compareRating${suffix}`
    );


  const description =
    document.querySelector(
      `#desc${suffix}, #compareDescription${suffix}`
    );


  if (image) {

    image.innerHTML =
      product.imageUrl
        ? `
          <img
            src="${escapeHtml(
              product.imageUrl
            )}"
            alt="${escapeHtml(
              product.name ||
              "Product"
            )}"
          >
        `
        : `
          <div class="compare-placeholder">
            CM
          </div>
        `;

  }


  if (source) {

    source.textContent =
      product.live === true
        ? `LIVE · ${
            product.provider ||
            "SOURCE"
          }`
        : `DEMO · ${
            product.provider ||
            "SOURCE"
          }`;

  }


  if (name) {

    name.textContent =
      product.name ||
      "Unnamed Product";

  }


  if (price) {

    price.textContent =
      formatPrice(
        product.price
      );

  }


  if (rating) {

    rating.textContent =
      product.rating !== null &&
      product.rating !== undefined
        ? `★ ${product.rating}`
        : "Rating unavailable";

  }


  if (description) {

    description.textContent =
      product.description ||
      "No description available.";

  }

}


/* =========================================================
   RENDER COMPARISON RESULT
========================================================= */

function formatComparisonValue(value) {

  if (
    value === null ||
    value === undefined
  ) {
    return "—";
  }

  if (
    typeof value === "string" ||
    typeof value === "number" ||
    typeof value === "boolean"
  ) {
    return escapeHtml(value);
  }

  if (Array.isArray(value)) {

    return value
      .map(item => {

        if (
          typeof item === "object" &&
          item !== null
        ) {
          return escapeHtml(
            JSON.stringify(item)
          );
        }

        return escapeHtml(item);

      })
      .join("<br>");

  }

  if (typeof value === "object") {

    return Object.entries(value)
      .map(([key, item]) => {

        const readableKey =
          key
            .replace(
              /([A-Z])/g,
              " $1"
            )
            .replace(
              /^./,
              letter =>
                letter.toUpperCase()
            );

        let readableValue;

        if (
          typeof item === "object" &&
          item !== null
        ) {
          readableValue =
            JSON.stringify(item);
        } else {
          readableValue =
            String(item);
        }

        return `
          <div class="spec-row">

            <span>
              ${escapeHtml(readableKey)}
            </span>

            <strong>
              ${escapeHtml(readableValue)}
            </strong>

          </div>
        `;

      })
      .join("");

  }

  return escapeHtml(
    String(value)
  );

}

function renderComparison(
  data,
  productA,
  productB
) {

  const comparison =
    data?.comparison || data;


  const workspace =
    document.querySelector(
      "#workspace"
    );


  if (!workspace) {
    return;
  }


  /* =========================
     HEADINGS
  ========================= */

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
      productA?.name ||
      "Product A";

  }


  if (headB) {

    headB.textContent =
      productB?.name ||
      "Product B";

  }


  /* =========================
     TRADE-OFF
  ========================= */

  const tradeOff =
    document.querySelector(
      "#tradeOffSummary"
    );


  if (tradeOff) {

    tradeOff.textContent =
      comparison?.tradeOffSummary ||
      "No trade-off summary available.";

  }


  /* =========================
     TABLE BODY
  ========================= */

  const body =
    document.querySelector(
      "#comparisonBody"
    );


  if (!body) {
    return;
  }


  /* =========================
     KEY DIFFERENCES
  ========================= */

  const keyDifferences =
    Array.isArray(
      comparison?.keyDifferences
    )
      ? comparison.keyDifferences
          .map(
            item =>
              escapeHtml(item)
          )
          .join("<br>")
      : formatComparisonValue(
          comparison?.keyDifferences ||
          "—"
        );


  /* =========================
     BACKEND SPECIFICATION
  ========================= */

  const specificationComparison =
    comparison
      ?.specificationComparison ||
      "—";


  /* =========================
     PRODUCT SPECIFICATIONS
  ========================= */

  const specificationsA =
    formatAttributes(
      productA?.attributes
    ) ||
    "Unavailable";


  const specificationsB =
    formatAttributes(
      productB?.attributes
    ) ||
    "Unavailable";


  /* =========================
     BUILD TABLE
  ========================= */

  body.innerHTML = `

    <tr>

      <td>
        Price
      </td>

      <td>
        ${formatPrice(
          productA?.price
        )}
      </td>

      <td>
        ${formatPrice(
          productB?.price
        )}
      </td>

    </tr>


    <tr>

      <td>
        Rating
      </td>

      <td>
        ${
          productA?.rating !== null &&
          productA?.rating !== undefined
            ? escapeHtml(
                productA.rating
              )
            : "Unavailable"
        }
      </td>

      <td>
        ${
          productB?.rating !== null &&
          productB?.rating !== undefined
            ? escapeHtml(
                productB.rating
              )
            : "Unavailable"
        }
      </td>

    </tr>


    <tr>

      <td>
        Availability
      </td>

      <td>
        ${
          escapeHtml(
            productA?.availability ||
            "Unavailable"
          )
        }
      </td>

      <td>
        ${
          escapeHtml(
            productB?.availability ||
            "Unavailable"
          )
        }
      </td>

    </tr>


    <tr>

      <td>
        Specifications
      </td>

      <td>
        ${specificationsA}
      </td>

      <td>
        ${specificationsB}
      </td>

    </tr>


    <tr>

      <td>
        Backend Comparison
      </td>

      <td colspan="2">
        ${
          formatComparisonValue(
            specificationComparison
          )
        }
      </td>

    </tr>


    <tr>

      <td>
        Key Differences
      </td>

      <td colspan="2">
        ${keyDifferences}
      </td>

    </tr>

  `;


  /* =========================
     SHOW RESULT
  ========================= */

  workspace.style.display =
    "block";


  workspace.classList.remove(
    "hidden"
  );


  workspace.scrollIntoView({
    behavior: "smooth",
    block: "start"
  });

}

/* =========================================================
   MAIN APPLICATION
========================================================= */

document.addEventListener(
  "DOMContentLoaded",
  () => {


    /* =====================================================
       NAVIGATION MENU
    ===================================================== */

    document
      .querySelectorAll(".menu")
      .forEach(
        button => {

          button.addEventListener(
            "click",
            () => {

              const mobileMenu =
                document.querySelector(
                  ".mobile-menu"
                );


              if (mobileMenu) {

                mobileMenu.classList.toggle(
                  "open"
                );

              }

            }
          );

        }
      );


    /* =====================================================
       HOME QUICK SEARCH
    ===================================================== */

    document
      .querySelectorAll(
        "[data-query]"
      )
      .forEach(
        button => {

          button.addEventListener(
            "click",
            () => {

              const input =
                document.querySelector(
                  "#searchInput"
                );


              if (!input) {

                return;

              }


              input.value =
                button.dataset.query;


              input.focus();

            }
          );

        }
      );


    /* =====================================================
       HOME SEARCH
    ===================================================== */

    const homeForm =
      document.querySelector(
        "#searchForm"
      );


    if (homeForm) {

      homeForm.addEventListener(
        "submit",
        async event => {

          event.preventDefault();


          const input =
            document.querySelector(
              "#searchInput"
            );


          const query =
            input?.value.trim();


          if (!query) {

            alert("Please enter a search query.");
            return;

          }

          const button =
            homeForm.querySelector(
              "button"
            );

          const originalText =
            button?.textContent;


          try {

            if (button) {

              button.disabled =
                true;

              button.textContent =
                "Thinking...";

            }


            const data =
              await CMApi.search(
                query
              );


            localStorage.setItem(
              "cm_q",
              query
            );


            localStorage.setItem(
              "cm_r",
              JSON.stringify(data)
            );


            window.location.href =
              "products.html";


          } catch (error) {

            console.error(
              "Home search error:",
              error
            );


            alert(
              error.message ||
              "Something went wrong."
            );


          } finally {

            if (button) {

              button.disabled =
                false;

              button.textContent =
                originalText ||
                "Search with AI →";

            }

          }

        }
      );

    }


    /* =====================================================
       PRODUCTS PAGE
    ===================================================== */

    const productsForm =
      document.querySelector(
        "#productsSearchForm"
      );


    const savedResults =
      localStorage.getItem(
        "cm_r"
      );


    if (savedResults) {

      try {

        const data =
          JSON.parse(
            savedResults
          );

        renderProducts(data);

        renderRecommendations(data);

      } catch (error) {

        console.error(
          "Saved result error:",
          error
        );

      }

    } else {
      const emptyState = document.querySelector("#productsEmpty");
      if (emptyState) {
        emptyState.style.display = "block";
      }
    }


    /* =====================================================
       PRODUCTS SEARCH
    ===================================================== */

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


          if (!query) {

            alert("Please enter a search query.");
            return;

          }


          const button =
            productsForm.querySelector(
              "button"
            );


          const originalText =
            button?.textContent;


          try {

            if (button) {

              button.disabled =
                true;

              button.textContent =
                "Searching...";

            }


            const data =
              await CMApi.search(
                query
              );


            localStorage.setItem(
              "cm_q",
              query
            );


            localStorage.setItem(
              "cm_r",
              JSON.stringify(data)
            );


            renderProducts(
              data
            );

            renderRecommendations(
              data
            );


          } catch (error) {

            console.error(
              "Product search error:",
              error
            );


            alert(
              error.message ||
              "Product search failed."
            );


          } finally {

            if (button) {

              button.disabled =
                false;

              button.textContent =
                originalText ||
                "Search";

            }

          }

        }
      );

    }


    /* =====================================================
       COMPARE PAGE
    ===================================================== */

    const compareA =
      document.querySelector(
        "#compareA"
      );


    const compareB =
      document.querySelector(
        "#compareB"
      );


    const compareButton =
      document.querySelector(
        "#compareButton"
      );


    if (
      compareA &&
      compareB
    ) {

      const products =
        loadCompareProducts();


      /* ================================================
         SHOW LATEST QUERY
      ================================================ */

      const latestQuery =
        document.querySelector(
          "#compareQuery, #latestQuery"
        );


      const savedQuery =
        localStorage.getItem(
          "cm_q"
        );


      if (
        latestQuery &&
        savedQuery
      ) {

        latestQuery.textContent =
          `Latest search: ${savedQuery}`;

      }


      /* ================================================
         PRODUCT A CHANGE
      ================================================ */

      compareA.addEventListener(
        "change",
        () => {

          const id =
            Number(
              compareA.value
            );


          const product =
            products.find(
              item =>
                Number(item.id) ===
                id
            );


          updateComparePreview(
            product,
            "A"
          );

        }
      );


      /* ================================================
         PRODUCT B CHANGE
      ================================================ */

      compareB.addEventListener(
        "change",
        () => {

          const id =
            Number(
              compareB.value
            );


          const product =
            products.find(
              item =>
                Number(item.id) ===
                id
            );


          updateComparePreview(
            product,
            "B"
          );

        }
      );


      /* ================================================
         BUILD COMPARISON
      ================================================ */

      if (compareButton) {

        compareButton.addEventListener(
          "click",
          async () => {

            const productIdA =
              Number(
                compareA.value
              );


            const productIdB =
              Number(
                compareB.value
              );


            if (
              !productIdA ||
              !productIdB
            ) {

              alert(
                "Please select both products."
              );

              return;

            }


            if (
              productIdA ===
              productIdB
            ) {

              alert(
                "Please select two different products."
              );

              return;

            }


            const productA =
              products.find(
                item =>
                  Number(item.id) ===
                  productIdA
              );


            const productB =
              products.find(
                item =>
                  Number(item.id) ===
                  productIdB
              );


            const originalText =
              compareButton.textContent;


            try {

              compareButton.disabled =
                true;


              compareButton.textContent =
                "Comparing...";


              const data =
                await CMApi.compareProducts(
                  [
                    productIdA,
                    productIdB
                  ]
                );


              renderComparison(
                data,
                productA,
                productB
              );


            } catch (error) {

              console.error(
                "Compare error:",
                error
              );


              alert(
                error.message ||
                "Unable to compare products."
              );


            } finally {

              compareButton.disabled =
                false;


              compareButton.textContent =
                originalText ||
                "Compare Products →";

            }

          }
        );

      }

    }


    /* =====================================================
   AI ASSISTANT
===================================================== */

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


      if (!query) {
        alert("Please enter a search query.");
        return;
      }


      const messages =
        document.querySelector(
          "#messages"
        );


      if (!messages) {
        return;
      }


      /* =========================
         USER MESSAGE
      ========================= */

      const userMessage =
        document.createElement(
          "div"
        );


      userMessage.className =
        "msg user";


      userMessage.textContent =
        query;


      messages.appendChild(
        userMessage
      );


      input.value = "";


      /* =========================
         BUTTON
      ========================= */

      const button =
        assistantForm.querySelector(
          "button"
        );


      const originalText =
        button?.textContent;


      try {

        if (button) {

          button.disabled =
            true;

          button.textContent =
            "Thinking...";

        }


        /* =========================
           SEARCH BACKEND
        ========================= */

        const data =
          await CMApi.search(
            query
          );


        /* =========================
           SAVE LATEST SEARCH
        ========================= */

        localStorage.setItem(
          "cm_q",
          query
        );


        localStorage.setItem(
          "cm_r",
          JSON.stringify(
            data
          )
        );


        /* =========================
           GET RESULTS
        ========================= */

        const rankedItems =
          Array.isArray(
            data?.rankedProducts
          )
            ? data.rankedProducts
            : [];

        const items =
          rankedItems.length
            ? rankedItems
            : (
                data?.products || []
              ).map(
                product => ({
                  product
                })
              );

        const scoreByExternalProductId =
          new Map(
            rankedItems
              .filter(
                item =>
                  item?.product
                    ?.externalProductId
              )
              .map(item => [
                item.product.externalProductId,
                item?.scoreBreakdown
                  ?.totalScore
              ])
          );


        /* =========================
           NO RESULTS
        ========================= */

        if (!items.length) {

          const aiMessage =
            document.createElement(
              "div"
            );


          aiMessage.className =
            "msg ai";


          aiMessage.innerHTML = `
            I couldn't find a matching product
            for your current requirements.

            <br><br>

            Try changing your budget,
            category, or specifications.
          `;


          messages.appendChild(
            aiMessage
          );


          messages.scrollTop =
            messages.scrollHeight;


          return;

        }


        /* =========================
           BUILD AI RESPONSE
        ========================= */

        const maxResults =
          Math.min(
            items.length,
            3
          );


        let resultHtml = `
          I found
          <strong>
            ${items.length}
          </strong>
          matching product(s).
          <br><br>
        `;


        for (
          let index = 0;
          index < maxResults;
          index++
        ) {

          const item =
            items[index];


          const product =
            item?.product ||
            item;


          const score =
            product?.externalProductId
              ? scoreByExternalProductId.get(
                  product.externalProductId
                )
              : item?.scoreBreakdown
                  ?.totalScore;


          const productName =
            product?.name ||
            "Unnamed Product";


          const price =
            formatPrice(
              product?.price
            );


          const rating =
            product?.rating !== null &&
            product?.rating !== undefined
              ? `★ ${product.rating}`
              : "Rating unavailable";


          const provider =
            product?.provider ||
            "Unknown source";


          const sourceLabel =
            product?.live === true
              ? `LIVE · ${provider}`
              : `DEMO · ${provider}`;


          resultHtml += `

            <div
              style="
                margin-top:12px;
                padding:12px;
                border-radius:12px;
                background:rgba(255,255,255,.05);
              "
            >

              <strong>
                ${index + 1}.
                ${escapeHtml(
                  productName
                )}
              </strong>

              <br>

              ${price}

              &nbsp; · &nbsp;

              ${escapeHtml(
                rating
              )}

              <br>

              <small>
                ${escapeHtml(
                  sourceLabel
                )}
              </small>

              ${
                score !== undefined &&
                score !== null
                  ? `
                    <br>

                    <small>
                      CartMind Score:
                      <strong>
                        ${Math.round(
                          score
                        )}/100
                      </strong>
                    </small>
                  `
                  : ""
              }

            </div>

          `;

        }


        /* =========================
           NEXT STEP MESSAGE
        ========================= */

        resultHtml += `

          <br>

          <a
            href="products.html"
            class="btn"
            style="display:inline-block"
          >
            View All Products →
          </a>

        `;


        const aiMessage =
          document.createElement(
            "div"
          );


        aiMessage.className =
          "msg ai";


        aiMessage.innerHTML =
          resultHtml;


        messages.appendChild(
          aiMessage
        );


        messages.scrollTop =
          messages.scrollHeight;


      } catch (error) {

        console.error(
          "AI Assistant error:",
          error
        );


        const errorMessage =
          document.createElement(
            "div"
          );


        errorMessage.className =
          "msg ai";


        errorMessage.textContent =
          error.message ||
          "Unable to process the request.";


        messages.appendChild(
          errorMessage
        );


        messages.scrollTop =
          messages.scrollHeight;


      } finally {

        if (button) {

          button.disabled =
            false;

          button.textContent =
            originalText ||
            "Send →";

        }

      }

    }
  );

}


    /* =====================================================
       REGISTER
    ===================================================== */

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
              .forEach(
                rule => {

                  rule.classList.toggle(
                    "ok",
                    Boolean(
                      rules[
                        rule.dataset.rule
                      ]
                    )
                  );

                }
              );

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
              passwordValue ===
                confirmPassword &&
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


    /* =====================================================
       LOGIN
    ===================================================== */

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

  }
);