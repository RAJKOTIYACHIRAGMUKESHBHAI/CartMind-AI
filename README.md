CartMind AI
=============

One-line description
--------------------
AI-powered assisted shopping explorer (demo): find and compare products using a local backend pipeline and deterministic ranking.

Problem statement
-----------------
Shoppers face information overload and opaque vendor listings. CartMind AI extracts shopper requirements from natural language, queries available product providers, ranks and explains results to help users compare and decide.

Solution / How CartMind works
----------------------------
- The frontend (static HTML/CSS/JS) sends a natural-language query to the backend search API.
- The backend extracts structured requirements (with a Bedrock-enabled extractor and a fallback parser), orchestrates provider calls, normalizes product data, deterministically ranks products, and returns recommendations and provider results.
- The frontend renders the returned products, recommendations, and enables details, comparison, and a small conversational assistant.

Key features currently implemented
----------------------------------
- Natural-language search endpoint: POST /api/ai/search
- Deterministic ranking with explainable score breakdown
- Recommendations buckets: BEST_OVERALL, BEST_MATCH, BEST_PRICE (live-only), BEST_RATED
- Product details page using saved search results
- Product comparison (2–3 products): POST /api/ai/compare
- "Why this?" grounded explanation endpoint: POST /api/ai/explain (uses Bedrock when available; deterministic fallback when not)
- Local/demo provider (LocalCatalogProvider) for offline/demo data
- Frontend assistant that calls the same search API and presents results
- Health endpoints: GET /actuator/health (actuator) and GET /api/health

High-level architecture
-----------------------
- Static frontend (frontend/) — HTML/CSS/JS pages that call a local backend API base (window.CM.apiBase)
- Backend (backend/) — Spring Boot application that provides the AI search pipeline, providers, ranking, recommendations, and explanation services
- LocalCatalogProvider supplies DEMO product data when live providers are not configured

Technology stack
----------------
- Backend: Java 17, Spring Boot, Maven
- Persistence: MySQL (used via Spring Data JPA)
- Frontend: static HTML, CSS, JavaScript (no SPA framework)
- Optional AI: AWS Bedrock (AWS SDK v2 integrated), with a local fallback parser

Backend structure and important components
------------------------------------------
(see backend/src/main/java/com/cartmind)
- controller: REST controllers (AiSearchController, HealthController)
- service: core services (RankingService, RecommendationService, ExplanationService, RequirementExtractor, OrchestratorService, ComparisonService)
- provider: provider implementations (LocalCatalogProvider and commerce provider abstractions)
- dto: request/response DTOs (SearchRequest, SearchResponse, ExtractedRequirements, CompareRequest/Response)
- model: domain models (NormalizedProduct, RankedProduct, ProviderResult, RecommendationType)
- entity/repository: JPA entities and Spring Data repositories for persisted products
- prompt: Bedrock/LLM prompt templates
- config: AWS / Bedrock client configuration

Frontend structure
------------------
(see frontend/)
- HTML pages: index.html, products.html, compare.html, assistant.html, details.html, login.html, register.html
- js/: config.js (api base), api.js (client wrapper for /api/ai/*), app.js (main integration, rendering, forms), assistant.js, recommendations.js, and smaller helpers
- css/: main styling used by pages
- assets/: images and logos

MySQL + Spring Data JPA information
----------------------------------
- Connection configured in backend/src/main/resources/application.properties. Defaults use environment variables; example property keys:
  - spring.datasource.url (DB_URL)
  - spring.datasource.username (DB_USERNAME)
  - spring.datasource.password (use env var)
- Spring Data JPA repositories are present and used by services. The project is designed to run against a local MySQL instance for full search/compare functionality.

Current demo-data / trust limitation (IMPORTANT)
------------------------------------------------
- LocalCatalogProvider is DEMO data and is explicitly marked as DEMO (provider name LOCAL_DEMO, live=false, availability DEMO_ONLY).
- Demo catalog is for demo/testing only. DO NOT treat demo prices, ratings, availability, or specifications as live commerce data.
- The system does NOT scrape or fabricate live vendor data. LIVE provider integrations require external provider authorization and are environment-dependent.

API endpoints implemented
-------------------------
- POST /api/ai/search
  - Request: { "query": "..." }
  - Response: SearchResponse JSON including: query, requirements, products, rankedProducts, providerResults, recommendations, message

- POST /api/ai/explain
  - Request: { "query": "...", "productId": <id> }
  - Response: Plain text explanation (uses Bedrock when available; otherwise deterministic fallback grounded in product data). Returns 404 if productId not found.

- POST /api/ai/compare
  - Request: { "productIds": [id1, id2, ...] } (2–3 ids)
  - Response: CompareResponse JSON including comparison data, price/rating/spec diffs, key differences, trade-off summary

- GET /actuator/health
  - Actuator health endpoint (returns standard actuator health JSON)

- GET /api/health
  - Simple health message endpoint

Local setup and run instructions (development)
----------------------------------------------
Prerequisites:
- Java 17 installed
- Maven (project includes Maven wrapper ./mvnw)
- MySQL (optional for demo; LocalCatalogProvider can run without a live DB for demo responses)
- (Optional) AWS credentials/environment if you plan to enable Bedrock integration

Backend (start locally):
1. Configure environment variables if you want non-default DB or AWS settings. Example (Windows PowerShell):
   $env:DB_URL='jdbc:mysql://localhost:3306/cartmind?useSSL=false&serverTimezone=Asia/Kolkata'
   $env:DB_USERNAME='root'
   $env:DB_PASSWORD='your_db_password'
   $env:AWS_REGION='us-east-1'  # if using Bedrock
2. From repository root:
   cd backend
   .\mvnw spring-boot:run
3. Verify health:
   curl http://localhost:8080/actuator/health

Frontend (serve static files locally):
1. From repository root:
   cd frontend
   # Use any static server. Example (Python 3):
   python -m http.server 5500
2. Open browser: http://localhost:5500/index.html
3. The frontend expects the backend API at window.CM.apiBase (default: http://localhost:8080). Adjust frontend/js/config.js if necessary.

Project folder structure (top-level)
------------------------------------
- backend/        # Spring Boot application (Java 17, Maven)
  - src/main/java/com/cartmind/... (controllers, services, providers, dto, model, entity, repository)
  - src/main/resources/application.properties
  - pom.xml
- frontend/       # static site (HTML, CSS, JS)
  - index.html, products.html, details.html, compare.html, assistant.html, login/register
  - js/, css/, assets/
- README.md       # <-- you are here

Testing instructions
--------------------
- Backend unit tests (run from backend/):
  ./mvnw test
- Build/compile backend:
  ./mvnw clean compile
- API smoke tests (examples):
  - Health: curl http://localhost:8080/actuator/health
  - Search: curl -X POST http://localhost:8080/api/ai/search -H "Content-Type: application/json" -d '{"query":"laptop under 50000 with 16GB RAM and SSD"}'
  - Explain: curl -X POST http://localhost:8080/api/ai/explain -H "Content-Type: application/json" -d '{"query":"...","productId":1}'
  - Compare: curl -X POST http://localhost:8080/api/ai/compare -H "Content-Type: application/json" -d '{"productIds":[1,2]}'

Future scope (not implemented / out of scope currently)
------------------------------------------------------
- Live provider integrations and authorized live pricing (requires external provider access)
- Scraping or bypassing provider authorization (not supported and intentionally out of scope)
- React/Next.js or SPA migration (frontend is intentionally static HTML/JS for the demo)
- Persistent user authentication and checkout flow
- Bedrock fine-tuning, agents, or RAG-style document retrieval beyond the current prompt-based integration

License
-------
This repository does not include a license file. If you intend to reuse or publish this code beyond internal/demo use, add a suitable open-source license file (e.g., MIT) or consult the project owner.

Notes / Trust & Safety
----------------------
- Demo/local provider data is DEMO_ONLY. Always surface "DEMO" or equivalent labeling in any UI for demo-derived results.
- Do not commit secrets (AWS keys, DB passwords) into the repository. Use environment variables or secret stores.

Contact / Maintainers
---------------------
- Project owner: repository maintainer (consult repository settings for contact)


----
Generated from the repository source. Keep the code and configuration in this repository unchanged when following these instructions.