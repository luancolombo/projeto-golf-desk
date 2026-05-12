const PLAYER_API_URL = "/player";
const TEE_TIME_API_URL = "/tee-time";
const BOOKING_API_URL = "/booking";
const BOOKING_PLAYER_API_URL = "/booking-player";
const RENTAL_ITEM_API_URL = "/rental-item";
const RENTAL_TRANSACTION_API_URL = "/rental-transaction";
const PAYMENT_API_URL = "/payment";

const tabButtons = document.querySelectorAll("[data-view]");
const playersView = document.getElementById("players-view");
const agendaView = document.getElementById("agenda-view");
const materialsView = document.getElementById("materials-view");
const teeTimesView = document.getElementById("tee-times-view");
const bookingsView = document.getElementById("bookings-view");
const agendaDateInput = document.getElementById("agenda-date-input");
const agendaTodayButton = document.getElementById("agenda-today-button");
const agendaRefreshButton = document.getElementById("agenda-refresh-button");
const agendaSlotsGrid = document.getElementById("agenda-slots-grid");
const agendaDayTitle = document.getElementById("agenda-day-title");
const agendaFeedback = document.getElementById("agenda-feedback");

const form = document.getElementById("player-form");
const formTitle = document.getElementById("form-title");
const clearButton = document.getElementById("clear-button");
const tableBody = document.getElementById("players-table-body");
const feedback = document.getElementById("feedback");
const searchNameInput = document.getElementById("search-name-input");
const searchNameButton = document.getElementById("search-name-button");
const searchIdInput = document.getElementById("search-id-input");
const searchIdButton = document.getElementById("search-id-button");
const findAllButton = document.getElementById("find-all-button");
const playerCount = document.getElementById("player-count");
const apiStatus = document.getElementById("api-status");
const requestJson = document.getElementById("request-json");
const responseJson = document.getElementById("response-json");

const fields = {
    id: document.getElementById("player-id"),
    fullName: document.getElementById("fullName"),
    taxNumber: document.getElementById("taxNumber"),
    email: document.getElementById("email"),
    phone: document.getElementById("phone"),
    handCap: document.getElementById("handCap"),
    member: document.getElementById("member"),
    notes: document.getElementById("notes")
};

const teeTimeForm = document.getElementById("tee-time-form");
const teeTimeFormTitle = document.getElementById("tee-time-form-title");
const teeTimeClearButton = document.getElementById("tee-time-clear-button");
const teeTimesTableBody = document.getElementById("tee-times-table-body");
const teeTimeFeedback = document.getElementById("tee-time-feedback");
const teeTimeSearchIdInput = document.getElementById("tee-time-search-id-input");
const teeTimeSearchIdButton = document.getElementById("tee-time-search-id-button");
const teeTimeFindAllButton = document.getElementById("tee-time-find-all-button");
const teeTimeCount = document.getElementById("tee-time-count");

const teeTimeFields = {
    id: document.getElementById("tee-time-id"),
    playDate: document.getElementById("playDate"),
    startTime: document.getElementById("startTime"),
    maxPlayers: document.getElementById("maxPlayers"),
    bookedPlayers: document.getElementById("bookedPlayers"),
    status: document.getElementById("teeTimeStatus"),
    baseGreenFee: document.getElementById("baseGreenFee")
};

const bookingForm = document.getElementById("booking-form");
const bookingFormTitle = document.getElementById("booking-form-title");
const bookingClearButton = document.getElementById("booking-clear-button");
const bookingsTableBody = document.getElementById("bookings-table-body");
const bookingFeedback = document.getElementById("booking-feedback");
const bookingSearchIdInput = document.getElementById("booking-search-id-input");
const bookingSearchIdButton = document.getElementById("booking-search-id-button");
const bookingFindAllButton = document.getElementById("booking-find-all-button");
const bookingRefreshContextButton = document.getElementById("booking-refresh-context-button");
const bookingCount = document.getElementById("booking-count");

const bookingFields = {
    id: document.getElementById("booking-id"),
    code: document.getElementById("bookingCode"),
    teeTimeId: document.getElementById("bookingTeeTimeId"),
    status: document.getElementById("bookingStatus"),
    totalAmount: document.getElementById("bookingTotalAmount"),
    createdBy: document.getElementById("bookingCreatedBy"),
    createdAt: document.getElementById("bookingCreatedAt")
};

const bookingPlayerForm = document.getElementById("booking-player-form");
const bookingPlayerFormTitle = document.getElementById("booking-player-form-title");
const bookingPlayerClearButton = document.getElementById("booking-player-clear-button");
const bookingPlayerFeedback = document.getElementById("booking-player-feedback");
const bookingPlayersTableBody = document.getElementById("booking-players-table-body");
const bookingPlayerCount = document.getElementById("booking-player-count");
const selectedBookingTitle = document.getElementById("selected-booking-title");

const bookingPlayerFields = {
    id: document.getElementById("booking-player-id"),
    bookingId: document.getElementById("bookingPlayerBookingId"),
    playerId: document.getElementById("bookingPlayerPlayerId"),
    greenFeeAmount: document.getElementById("bookingPlayerGreenFeeAmount"),
    checkedIn: document.getElementById("bookingPlayerCheckedIn")
};

const rentalTransactionForm = document.getElementById("rental-transaction-form");
const rentalTransactionFormTitle = document.getElementById("rental-transaction-form-title");
const rentalTransactionClearButton = document.getElementById("rental-transaction-clear-button");
const rentalTransactionFeedback = document.getElementById("rental-transaction-feedback");
const rentalTransactionsTableBody = document.getElementById("rental-transactions-table-body");
const rentalTransactionCount = document.getElementById("rental-transaction-count");
const selectedRentalTitle = document.getElementById("selected-rental-title");
const returnAllRentalsButton = document.getElementById("return-all-rentals-button");

const rentalTransactionFields = {
    id: document.getElementById("rental-transaction-id"),
    bookingId: document.getElementById("rentalTransactionBookingId"),
    bookingPlayerId: document.getElementById("rentalTransactionBookingPlayerId"),
    rentalItemId: document.getElementById("rentalTransactionRentalItemId"),
    quantity: document.getElementById("rentalTransactionQuantity"),
    status: document.getElementById("rentalTransactionStatus"),
    unitPrice: document.getElementById("rentalTransactionUnitPrice"),
    totalPrice: document.getElementById("rentalTransactionTotalPrice")
};

const paymentForm = document.getElementById("payment-form");
const paymentFormTitle = document.getElementById("payment-form-title");
const paymentClearButton = document.getElementById("payment-clear-button");
const paymentFeedback = document.getElementById("payment-feedback");
const paymentsTableBody = document.getElementById("payments-table-body");
const paymentCount = document.getElementById("payment-count");
const selectedPaymentTitle = document.getElementById("selected-payment-title");

const paymentFields = {
    id: document.getElementById("payment-id"),
    bookingId: document.getElementById("paymentBookingId"),
    bookingPlayerId: document.getElementById("paymentBookingPlayerId"),
    amount: document.getElementById("paymentAmount"),
    method: document.getElementById("paymentMethod"),
    status: document.getElementById("paymentStatus"),
    paidAt: document.getElementById("paymentPaidAt")
};

const rentalItemForm = document.getElementById("rental-item-form");
const rentalItemFormTitle = document.getElementById("rental-item-form-title");
const rentalItemClearButton = document.getElementById("rental-item-clear-button");
const rentalItemFindAllButton = document.getElementById("rental-item-find-all-button");
const rentalItemFeedback = document.getElementById("rental-item-feedback");
const rentalItemsTableBody = document.getElementById("rental-items-table-body");
const rentalItemCount = document.getElementById("rental-item-count");

const rentalItemFields = {
    id: document.getElementById("rental-item-id"),
    name: document.getElementById("rentalItemName"),
    type: document.getElementById("rentalItemType"),
    totalStock: document.getElementById("rentalItemTotalStock"),
    availableStock: document.getElementById("rentalItemAvailableStock"),
    rentalPrice: document.getElementById("rentalItemRentalPrice"),
    active: document.getElementById("rentalItemActive")
};

let players = [];
let visiblePlayers = [];
let teeTimes = [];
let visibleTeeTimes = [];
let bookings = [];
let visibleBookings = [];
let bookingPlayers = [];
let rentalItems = [];
let visibleRentalItems = [];
let rentalTransactions = [];
let payments = [];
let selectedBookingId = null;

document.addEventListener("DOMContentLoaded", () => {
    agendaDateInput.value = todayIsoDate();
    bindEvents();
    switchView("players");
    setFeedback("Aguardando comando para consultar a API.", "success");
    setTeeTimeFeedback("Aguardando comando para consultar a agenda.", "success");
    setBookingFeedback("Aguardando comando para consultar bookings.", "success");
    setBookingPlayerFeedback("Selecione um booking para adicionar jogadores.", "success");
    setRentalTransactionFeedback("Selecione um booking para adicionar alugueres.", "success");
    setPaymentFeedback("Selecione um booking para registrar pagamentos.", "success");
    setRentalItemFeedback("Cadastre ou consulte itens de aluguer.", "success");
    setAgendaFeedback("Escolha um dia para ver os horarios disponiveis.", "success");
    updateReturnAllRentalsButton();
    apiStatus.textContent = "Aguardando consulta";
});

function bindEvents() {
    tabButtons.forEach((button) => {
        button.addEventListener("click", () => switchView(button.dataset.view));
    });

    form.addEventListener("submit", handleSubmit);
    clearButton.addEventListener("click", resetForm);
    findAllButton.addEventListener("click", loadPlayers);
    searchNameButton.addEventListener("click", handleSearchByName);
    searchNameInput.addEventListener("keydown", (event) => {
        if (event.key === "Enter") {
            event.preventDefault();
            handleSearchByName();
        }
    });
    searchIdButton.addEventListener("click", handleSearchById);
    searchIdInput.addEventListener("keydown", (event) => {
        if (event.key === "Enter") {
            event.preventDefault();
            handleSearchById();
        }
    });

    teeTimeForm.addEventListener("submit", handleTeeTimeSubmit);
    teeTimeClearButton.addEventListener("click", resetTeeTimeForm);
    teeTimeFindAllButton.addEventListener("click", loadTeeTimes);
    teeTimeSearchIdButton.addEventListener("click", handleTeeTimeSearchById);
    teeTimeSearchIdInput.addEventListener("keydown", (event) => {
        if (event.key === "Enter") {
            event.preventDefault();
            handleTeeTimeSearchById();
        }
    });

    bookingForm.addEventListener("submit", handleBookingSubmit);
    bookingClearButton.addEventListener("click", resetBookingForm);
    bookingFindAllButton.addEventListener("click", loadBookings);
    bookingRefreshContextButton.addEventListener("click", refreshBookingContext);
    bookingSearchIdButton.addEventListener("click", handleBookingSearchById);
    bookingSearchIdInput.addEventListener("keydown", (event) => {
        if (event.key === "Enter") {
            event.preventDefault();
            handleBookingSearchById();
        }
    });

    bookingPlayerForm.addEventListener("submit", handleBookingPlayerSubmit);
    bookingPlayerClearButton.addEventListener("click", resetBookingPlayerForm);

    rentalTransactionForm.addEventListener("submit", handleRentalTransactionSubmit);
    rentalTransactionClearButton.addEventListener("click", resetRentalTransactionForm);
    rentalTransactionFields.bookingPlayerId.addEventListener("change", updateRentalTransactionPricePreview);
    rentalTransactionFields.rentalItemId.addEventListener("change", updateRentalTransactionPricePreview);
    rentalTransactionFields.quantity.addEventListener("input", updateRentalTransactionPricePreview);
    returnAllRentalsButton.addEventListener("click", returnAllRentalTransactions);

    paymentForm.addEventListener("submit", handlePaymentSubmit);
    paymentClearButton.addEventListener("click", resetPaymentForm);
    paymentFields.bookingPlayerId.addEventListener("change", () => updatePaymentAmountPreview({ force: true }));

    rentalItemForm.addEventListener("submit", handleRentalItemSubmit);
    rentalItemClearButton.addEventListener("click", resetRentalItemForm);
    rentalItemFindAllButton.addEventListener("click", loadRentalItems);

    agendaDateInput.addEventListener("change", refreshBookingContext);
    agendaTodayButton.addEventListener("click", () => {
        agendaDateInput.value = todayIsoDate();
        refreshBookingContext();
    });
    agendaRefreshButton.addEventListener("click", refreshBookingContext);
}

function switchView(view) {
    const views = {
        players: playersView,
        agenda: agendaView,
        materials: materialsView
    };

    Object.entries(views).forEach(([viewName, element]) => {
        const isActive = viewName === view;
        element.classList.toggle("is-hidden", !isActive);
        element.hidden = !isActive;
    });

    tabButtons.forEach((button) => {
        button.classList.toggle("active", button.dataset.view === view);
        button.setAttribute("aria-selected", String(button.dataset.view === view));
    });

    if (view === "agenda") {
        refreshBookingContext();
    }

    if (view === "materials") {
        loadRentalItems();
    }
}

async function loadPlayers() {
    setFeedback("Carregando players...", "success");
    showRequest("GET", PLAYER_API_URL);

    try {
        const response = await fetch(PLAYER_API_URL);

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        players = await response.json();
        showResponse(players);
        apiStatus.textContent = "Conectada";
        renderPlayers();
        setFeedback("Players carregados com sucesso.", "success");
    } catch (error) {
        apiStatus.textContent = "Falha na conexao";
        tableBody.innerHTML = `<tr><td colspan="6" class="empty-state">${escapeHtml(error.message)}</td></tr>`;
        playerCount.textContent = "0 players";
        setFeedback(error.message, "error");
    }
}

async function handleSubmit(event) {
    event.preventDefault();

    const payload = getFormData();
    const isEditing = Boolean(payload.id);
    const method = isEditing ? "PUT" : "POST";
    showRequest(method, PLAYER_API_URL, payload);

    try {
        const response = await fetch(PLAYER_API_URL, {
            method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        const savedPlayer = await response.json();
        showResponse(savedPlayer);
        resetForm();
        await loadPlayers();
        setFeedback(
            isEditing ? "Player atualizado com sucesso." : "Player cadastrado com sucesso.",
            "success"
        );
    } catch (error) {
        setFeedback(error.message, "error");
    }
}

async function handleSearchById() {
    const id = searchIdInput.value.trim();

    if (!id) {
        clearJsonResponse();
        setFeedback("Informe um id para buscar.", "error");
        return;
    }

    showRequest("GET", `${PLAYER_API_URL}/${id}`);

    try {
        const response = await fetch(`${PLAYER_API_URL}/${id}`);

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        const player = await response.json();
        showResponse(player);
        apiStatus.textContent = "Conectada";
        renderPlayers([player]);
        setFeedback(`Player #${id} encontrado com sucesso.`, "success");
    } catch (error) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="6" class="empty-state">${escapeHtml(error.message)}</td>
            </tr>
        `;
        playerCount.textContent = "0 players";
        setFeedback(error.message, "error");
    }
}

async function handleSearchByName() {
    const name = searchNameInput.value.trim();

    if (!name) {
        clearJsonResponse();
        setFeedback("Informe um nome para buscar.", "error");
        return;
    }

    const url = `${PLAYER_API_URL}/search?name=${encodeURIComponent(name)}`;
    showRequest("GET", url);

    try {
        const response = await fetch(url);

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        const foundPlayers = await response.json();
        players = foundPlayers;
        showResponse(foundPlayers);
        apiStatus.textContent = "Conectada";
        renderPlayers(foundPlayers);

        if (foundPlayers.length === 0) {
            setFeedback(`Nenhum player encontrado com o nome "${name}".`, "error");
            return;
        }

        setFeedback(`${foundPlayers.length} player${foundPlayers.length === 1 ? "" : "s"} encontrado${foundPlayers.length === 1 ? "" : "s"} por nome.`, "success");
    } catch (error) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="6" class="empty-state">${escapeHtml(error.message)}</td>
            </tr>
        `;
        playerCount.textContent = "0 players";
        setFeedback(error.message, "error");
    }
}

function getFormData() {
    return {
        id: fields.id.value ? Number(fields.id.value) : null,
        fullName: fields.fullName.value.trim(),
        taxNumber: fields.taxNumber.value.trim(),
        email: fields.email.value.trim(),
        phone: fields.phone.value.trim(),
        handCap: fields.handCap.value.trim(),
        member: fields.member.checked,
        notes: fields.notes.value.trim()
    };
}

function renderPlayers(source = players) {
    visiblePlayers = source;
    playerCount.textContent = `${source.length} player${source.length === 1 ? "" : "s"}`;

    if (source.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="6" class="empty-state">Nenhum player encontrado.</td>
            </tr>
        `;
        return;
    }

    tableBody.innerHTML = source
        .map((player) => `
            <tr>
                <td>
                    <div class="row-main">${escapeHtml(player.fullName)}</div>
                    <div class="row-sub">${escapeHtml(player.notes || "Sem observacoes cadastradas.")}</div>
                </td>
                <td>${escapeHtml(player.taxNumber)}</td>
                <td>
                    <div class="row-main">${escapeHtml(player.email)}</div>
                    <div class="row-sub">${escapeHtml(player.phone)}</div>
                </td>
                <td>${escapeHtml(player.handCap || "-")}</td>
                <td>${player.member ? "Sim" : "Nao"}</td>
                <td>
                    <div class="table-actions">
                        <button class="action-button edit" type="button" data-player-action="edit" data-id="${player.id}">
                            Editar
                        </button>
                        <button class="action-button delete" type="button" data-player-action="delete" data-id="${player.id}">
                            Excluir
                        </button>
                    </div>
                </td>
            </tr>
        `)
        .join("");

    bindPlayerRowActions();
}

function bindPlayerRowActions() {
    document.querySelectorAll("[data-player-action='edit']").forEach((button) => {
        button.addEventListener("click", () => {
            const playerId = Number(button.dataset.id);
            const player = visiblePlayers.find((item) => item.id === playerId);

            if (!player) {
                return;
            }

            fillForm(player);
        });
    });

    document.querySelectorAll("[data-player-action='delete']").forEach((button) => {
        button.addEventListener("click", async () => {
            const playerId = Number(button.dataset.id);
            await deletePlayer(playerId);
        });
    });
}

function fillForm(player) {
    fields.id.value = player.id ?? "";
    fields.fullName.value = player.fullName ?? "";
    fields.taxNumber.value = player.taxNumber ?? "";
    fields.email.value = player.email ?? "";
    fields.phone.value = player.phone ?? "";
    fields.handCap.value = player.handCap ?? "";
    fields.member.checked = Boolean(player.member);
    fields.notes.value = player.notes ?? "";

    formTitle.textContent = `Editando #${player.id}`;
    window.scrollTo({ top: 0, behavior: "smooth" });
}

function resetForm() {
    form.reset();
    fields.id.value = "";
    formTitle.textContent = "Novo player";
}

async function deletePlayer(id) {
    const confirmed = window.confirm("Deseja realmente excluir este player?");

    if (!confirmed) {
        return;
    }

    showRequest("DELETE", `${PLAYER_API_URL}/${id}`);

    try {
        const response = await fetch(`${PLAYER_API_URL}/${id}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        showResponse({
            status: response.status,
            message: `Player ${id} excluido com sucesso.`
        });
        resetForm();
        await loadPlayers();
        setFeedback("Player excluido com sucesso.", "success");
    } catch (error) {
        setFeedback(error.message, "error");
    }
}

async function loadTeeTimes() {
    setTeeTimeFeedback("Carregando tee times...", "success");
    showRequest("GET", TEE_TIME_API_URL);

    try {
        const response = await fetch(TEE_TIME_API_URL);

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        teeTimes = sortTeeTimes(await response.json());
        showResponse(teeTimes);
        apiStatus.textContent = "Conectada";
        renderTeeTimes();
        renderDailyAgenda();
        setTeeTimeFeedback("Tee times carregados com sucesso.", "success");
    } catch (error) {
        apiStatus.textContent = "Falha na conexao";
        teeTimesTableBody.innerHTML = `<tr><td colspan="6" class="empty-state">${escapeHtml(error.message)}</td></tr>`;
        teeTimeCount.textContent = "0 tee times";
        setTeeTimeFeedback(error.message, "error");
    }
}

async function handleTeeTimeSubmit(event) {
    event.preventDefault();

    const payload = getTeeTimeFormData();
    const isEditing = Boolean(payload.id);
    const method = isEditing ? "PUT" : "POST";
    showRequest(method, TEE_TIME_API_URL, payload);

    try {
        const response = await fetch(TEE_TIME_API_URL, {
            method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        const savedTeeTime = await response.json();
        showResponse(savedTeeTime);
        resetTeeTimeForm();
        await loadTeeTimes();
        setTeeTimeFeedback(
            isEditing ? "Tee time atualizado com sucesso." : "Tee time cadastrado com sucesso.",
            "success"
        );
    } catch (error) {
        setTeeTimeFeedback(error.message, "error");
    }
}

async function handleTeeTimeSearchById() {
    const id = teeTimeSearchIdInput.value.trim();

    if (!id) {
        clearJsonResponse();
        setTeeTimeFeedback("Informe um id para buscar.", "error");
        return;
    }

    showRequest("GET", `${TEE_TIME_API_URL}/${id}`);

    try {
        const response = await fetch(`${TEE_TIME_API_URL}/${id}`);

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        const teeTime = await response.json();
        showResponse(teeTime);
        apiStatus.textContent = "Conectada";
        renderTeeTimes([teeTime]);
        setTeeTimeFeedback(`Tee time #${id} encontrado com sucesso.`, "success");
    } catch (error) {
        teeTimesTableBody.innerHTML = `
            <tr>
                <td colspan="6" class="empty-state">${escapeHtml(error.message)}</td>
            </tr>
        `;
        teeTimeCount.textContent = "0 tee times";
        setTeeTimeFeedback(error.message, "error");
    }
}

function getTeeTimeFormData() {
    return {
        id: teeTimeFields.id.value ? Number(teeTimeFields.id.value) : null,
        playDate: teeTimeFields.playDate.value,
        startTime: teeTimeFields.startTime.value,
        maxPlayers: Number(teeTimeFields.maxPlayers.value || 4),
        bookedPlayers: Number(teeTimeFields.bookedPlayers.value || 0),
        status: teeTimeFields.status.value,
        baseGreenFee: Number(teeTimeFields.baseGreenFee.value || 0)
    };
}

function renderTeeTimes(source = teeTimes) {
    visibleTeeTimes = source;
    teeTimeCount.textContent = `${source.length} tee time${source.length === 1 ? "" : "s"}`;

    if (source.length === 0) {
        teeTimesTableBody.innerHTML = `
            <tr>
                <td colspan="6" class="empty-state">Nenhum tee time encontrado.</td>
            </tr>
        `;
        return;
    }

    teeTimesTableBody.innerHTML = source
        .map((teeTime) => `
            <tr>
                <td>
                    <div class="row-main">${formatDate(teeTime.playDate)}</div>
                    <div class="row-sub">ID #${teeTime.id}</div>
                </td>
                <td>${formatTime(teeTime.startTime)}</td>
                <td>${teeTime.bookedPlayers}/${teeTime.maxPlayers}</td>
                <td><span class="status-pill">${escapeHtml(teeTime.status)}</span></td>
                <td>${formatMoney(teeTime.baseGreenFee)}</td>
                <td>
                    <div class="table-actions">
                        <button class="action-button edit" type="button" data-tee-time-action="edit" data-id="${teeTime.id}">
                            Editar
                        </button>
                        <button class="action-button delete" type="button" data-tee-time-action="delete" data-id="${teeTime.id}">
                            Excluir
                        </button>
                    </div>
                </td>
            </tr>
        `)
        .join("");

    bindTeeTimeRowActions();
}

function bindTeeTimeRowActions() {
    document.querySelectorAll("[data-tee-time-action='edit']").forEach((button) => {
        button.addEventListener("click", () => {
            const teeTimeId = Number(button.dataset.id);
            const teeTime = visibleTeeTimes.find((item) => item.id === teeTimeId);

            if (!teeTime) {
                return;
            }

            fillTeeTimeForm(teeTime);
        });
    });

    document.querySelectorAll("[data-tee-time-action='delete']").forEach((button) => {
        button.addEventListener("click", async () => {
            const teeTimeId = Number(button.dataset.id);
            await deleteTeeTime(teeTimeId);
        });
    });
}

function fillTeeTimeForm(teeTime) {
    teeTimeFields.id.value = teeTime.id ?? "";
    teeTimeFields.playDate.value = teeTime.playDate ?? "";
    teeTimeFields.startTime.value = formatTime(teeTime.startTime);
    teeTimeFields.maxPlayers.value = teeTime.maxPlayers ?? 4;
    teeTimeFields.bookedPlayers.value = teeTime.bookedPlayers ?? 0;
    teeTimeFields.status.value = teeTime.status ?? "AVAILABLE";
    teeTimeFields.baseGreenFee.value = teeTime.baseGreenFee ?? "0.00";

    teeTimeFormTitle.textContent = `Editando #${teeTime.id}`;
    window.scrollTo({ top: 0, behavior: "smooth" });
}

function resetTeeTimeForm() {
    teeTimeForm.reset();
    teeTimeFields.id.value = "";
    teeTimeFields.maxPlayers.value = 4;
    teeTimeFields.bookedPlayers.value = 0;
    teeTimeFields.status.value = "AVAILABLE";
    teeTimeFields.baseGreenFee.value = "0.00";
    teeTimeFormTitle.textContent = "Novo tee time";
}

async function deleteTeeTime(id) {
    const confirmed = window.confirm("Deseja realmente excluir este tee time?");

    if (!confirmed) {
        return;
    }

    showRequest("DELETE", `${TEE_TIME_API_URL}/${id}`);

    try {
        const response = await fetch(`${TEE_TIME_API_URL}/${id}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        showResponse({
            status: response.status,
            message: `Tee time ${id} excluido com sucesso.`
        });
        resetTeeTimeForm();
        await loadTeeTimes();
        setTeeTimeFeedback("Tee time excluido com sucesso.", "success");
    } catch (error) {
        setTeeTimeFeedback(error.message, "error");
    }
}

async function loadBookings() {
    setBookingFeedback("Carregando bookings...", "success");
    showRequest("GET", BOOKING_API_URL);

    try {
        await Promise.all([
            loadBookingPlayers({ silent: true }),
            loadRentalTransactions({ silent: true }),
            loadPayments({ silent: true })
        ]);
        const response = await fetch(BOOKING_API_URL);

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        bookings = sortBookings(await response.json());
        showResponse(bookings);
        apiStatus.textContent = "Conectada";
        renderBookings();
        renderSelectedBookingPlayers();
        renderSelectedRentalTransactions();
        renderSelectedPayments();
        renderDailyAgenda();
        setBookingFeedback("Bookings carregados com sucesso.", "success");
    } catch (error) {
        apiStatus.textContent = "Falha na conexao";
        bookingsTableBody.innerHTML = `<tr><td colspan="8" class="empty-state">${escapeHtml(error.message)}</td></tr>`;
        bookingCount.textContent = "0 bookings";
        setBookingFeedback(error.message, "error");
    }
}

async function handleBookingSubmit(event) {
    event.preventDefault();

    const payload = getBookingFormData();
    const isEditing = Boolean(payload.id);
    const method = isEditing ? "PUT" : "POST";
    showRequest(method, BOOKING_API_URL, payload);

    try {
        const response = await fetch(BOOKING_API_URL, {
            method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        const savedBooking = await response.json();
        showResponse(savedBooking);
        resetBookingForm();
        await loadBookings();
        selectBooking(savedBooking.id);
        setBookingFeedback(
            isEditing ? "Booking atualizado com sucesso." : "Booking cadastrado com sucesso.",
            "success"
        );
    } catch (error) {
        setBookingFeedback(error.message, "error");
    }
}

async function handleBookingSearchById() {
    const id = bookingSearchIdInput.value.trim();

    if (!id) {
        clearJsonResponse();
        setBookingFeedback("Informe um id para buscar.", "error");
        return;
    }

    showRequest("GET", `${BOOKING_API_URL}/${id}`);

    try {
        const response = await fetch(`${BOOKING_API_URL}/${id}`);

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        const booking = await response.json();
        showResponse(booking);
        apiStatus.textContent = "Conectada";
        renderBookings([booking]);
        selectBooking(booking.id);
        setBookingFeedback(`Booking #${id} encontrado com sucesso.`, "success");
    } catch (error) {
        bookingsTableBody.innerHTML = `
            <tr>
                <td colspan="8" class="empty-state">${escapeHtml(error.message)}</td>
            </tr>
        `;
        bookingCount.textContent = "0 bookings";
        setBookingFeedback(error.message, "error");
    }
}

function getBookingFormData() {
    return {
        id: bookingFields.id.value ? Number(bookingFields.id.value) : null,
        code: bookingFields.code.value.trim() || null,
        status: bookingFields.status.value,
        totalAmount: Number(bookingFields.totalAmount.value || 0),
        createdBy: bookingFields.createdBy.value ? Number(bookingFields.createdBy.value) : null,
        teeTimeId: Number(bookingFields.teeTimeId.value)
    };
}

function renderBookings(source = bookings) {
    visibleBookings = source;
    bookingCount.textContent = `${source.length} booking${source.length === 1 ? "" : "s"}`;

    if (source.length === 0) {
        bookingsTableBody.innerHTML = `
            <tr>
                <td colspan="8" class="empty-state">Nenhum booking encontrado.</td>
            </tr>
        `;
        return;
    }

    bookingsTableBody.innerHTML = source
        .map((booking) => `
            <tr>
                <td>
                    <div class="row-main">${escapeHtml(booking.code || "-")}</div>
                    <div class="row-sub">ID #${booking.id}</div>
                </td>
                <td>#${booking.teeTimeId}</td>
                <td><span class="status-pill">${escapeHtml(booking.status || "-")}</span></td>
                <td>${formatMoney(booking.totalAmount)}</td>
                <td>${getBookingPlayers(booking.id).length}</td>
                <td>${formatDateTime(booking.createdAt)}</td>
                <td>${booking.createdBy ?? "-"}</td>
                <td>
                    <div class="table-actions">
                        <button class="action-button select" type="button" data-booking-action="select" data-id="${booking.id}">
                            Selecionar
                        </button>
                        <button class="action-button edit" type="button" data-booking-action="edit" data-id="${booking.id}">
                            Editar
                        </button>
                        <button class="action-button delete" type="button" data-booking-action="delete" data-id="${booking.id}">
                            Excluir
                        </button>
                    </div>
                </td>
            </tr>
        `)
        .join("");

    bindBookingRowActions();
}

function bindBookingRowActions() {
    document.querySelectorAll("[data-booking-action='select']").forEach((button) => {
        button.addEventListener("click", () => {
            selectBooking(Number(button.dataset.id));
        });
    });

    document.querySelectorAll("[data-booking-action='edit']").forEach((button) => {
        button.addEventListener("click", () => {
            const bookingId = Number(button.dataset.id);
            const booking = visibleBookings.find((item) => item.id === bookingId);

            if (!booking) {
                return;
            }

            fillBookingForm(booking);
            selectBooking(booking.id);
        });
    });

    document.querySelectorAll("[data-booking-action='delete']").forEach((button) => {
        button.addEventListener("click", async () => {
            const bookingId = Number(button.dataset.id);
            await deleteBooking(bookingId);
        });
    });
}

function fillBookingForm(booking) {
    bookingFields.id.value = booking.id ?? "";
    bookingFields.code.value = booking.code ?? "";
    bookingFields.teeTimeId.value = booking.teeTimeId ?? "";
    bookingFields.status.value = booking.status ?? "CREATED";
    bookingFields.totalAmount.value = booking.totalAmount ?? "0.00";
    bookingFields.createdBy.value = booking.createdBy ?? "";
    bookingFields.createdAt.value = formatDateTime(booking.createdAt);

    bookingFormTitle.textContent = `Editando #${booking.id}`;
    bookingPlayerFields.bookingId.value = booking.id ?? "";
    rentalTransactionFields.bookingId.value = booking.id ?? "";
    paymentFields.bookingId.value = booking.id ?? "";
    window.scrollTo({ top: 0, behavior: "smooth" });
}

function resetBookingForm() {
    bookingForm.reset();
    bookingFields.id.value = "";
    bookingFields.code.value = "";
    bookingFields.status.value = "CREATED";
    bookingFields.totalAmount.value = "0.00";
    bookingFields.createdAt.value = "";
    bookingFormTitle.textContent = "Novo booking";
}

async function deleteBooking(id) {
    const confirmed = window.confirm("Deseja realmente excluir este booking?");

    if (!confirmed) {
        return;
    }

    showRequest("DELETE", `${BOOKING_API_URL}/${id}`);

    try {
        const response = await fetch(`${BOOKING_API_URL}/${id}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        showResponse({
            status: response.status,
            message: `Booking ${id} excluido com sucesso.`
        });
        resetBookingForm();
        if (selectedBookingId === id) {
            selectedBookingId = null;
            resetBookingPlayerForm();
            resetRentalTransactionForm();
            resetPaymentForm();
        }
        await loadBookings();
        setBookingFeedback("Booking excluido com sucesso.", "success");
    } catch (error) {
        setBookingFeedback(error.message, "error");
    }
}

async function refreshBookingContext() {
    try {
        await Promise.all([
            loadPlayersForSelect(),
            loadTeeTimesForSelect(),
            loadBookingPlayers({ silent: true }),
            loadRentalItemsForSelect(),
            loadRentalTransactions({ silent: true }),
            loadPayments({ silent: true })
        ]);
        renderTeeTimes();
        renderRentalItems();
        await loadBookings();
        renderSelectedBookingPlayers();
        renderSelectedRentalTransactions();
        renderSelectedPayments();
        renderDailyAgenda();
    } catch (error) {
        setBookingFeedback(error.message, "error");
        setAgendaFeedback(error.message, "error");
    }
}

async function loadPlayersForSelect() {
    const response = await fetch(PLAYER_API_URL);

    if (!response.ok) {
        throw new Error(await extractErrorMessage(response));
    }

    players = await response.json();
    populatePlayerOptions();
}

async function loadTeeTimesForSelect() {
    const response = await fetch(TEE_TIME_API_URL);

    if (!response.ok) {
        throw new Error(await extractErrorMessage(response));
    }

    teeTimes = sortTeeTimes(await response.json());
    populateTeeTimeOptions();
}

async function loadBookingPlayers({ silent = false } = {}) {
    if (!silent) {
        setBookingPlayerFeedback("Carregando jogadores do booking...", "success");
    }

    const response = await fetch(BOOKING_PLAYER_API_URL);

    if (!response.ok) {
        throw new Error(await extractErrorMessage(response));
    }

    bookingPlayers = await response.json();
}

function populatePlayerOptions() {
    const currentValue = bookingPlayerFields.playerId.value;
    bookingPlayerFields.playerId.innerHTML = `
        <option value="">Selecione um player</option>
        ${players.map((player) => `
            <option value="${player.id}">#${player.id} - ${escapeHtml(player.fullName)}</option>
        `).join("")}
    `;
    bookingPlayerFields.playerId.value = currentValue;
}

function populateTeeTimeOptions() {
    const currentValue = bookingFields.teeTimeId.value;
    bookingFields.teeTimeId.innerHTML = `
        <option value="">Selecione um tee time</option>
        ${teeTimes.map((teeTime) => `
            <option value="${teeTime.id}">
                #${teeTime.id} - ${formatDate(teeTime.playDate)} ${formatTime(teeTime.startTime)} - ${teeTime.bookedPlayers}/${teeTime.maxPlayers} - ${escapeHtml(teeTime.status)}
            </option>
        `).join("")}
    `;
    bookingFields.teeTimeId.value = currentValue;
}

async function loadRentalItems() {
    setRentalItemFeedback("Carregando itens de aluguer...", "success");
    showRequest("GET", RENTAL_ITEM_API_URL);

    try {
        const response = await fetch(RENTAL_ITEM_API_URL);

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        rentalItems = await response.json();
        showResponse(rentalItems);
        apiStatus.textContent = "Conectada";
        renderRentalItems();
        populateRentalItemOptions();
        setRentalItemFeedback("Itens de aluguer carregados com sucesso.", "success");
    } catch (error) {
        apiStatus.textContent = "Falha na conexao";
        rentalItemsTableBody.innerHTML = `<tr><td colspan="6" class="empty-state">${escapeHtml(error.message)}</td></tr>`;
        rentalItemCount.textContent = "0 itens";
        setRentalItemFeedback(error.message, "error");
    }
}

async function loadRentalItemsForSelect() {
    const response = await fetch(RENTAL_ITEM_API_URL);

    if (!response.ok) {
        throw new Error(await extractErrorMessage(response));
    }

    rentalItems = await response.json();
    populateRentalItemOptions();
}

function populateRentalItemOptions() {
    const currentValue = rentalTransactionFields.rentalItemId.value;
    const activeItems = rentalItems.filter((item) => Boolean(item.active));
    rentalTransactionFields.rentalItemId.innerHTML = `
        <option value="">Selecione um item</option>
        ${activeItems.map((item) => `
            <option value="${item.id}">
                #${item.id} - ${escapeHtml(item.name)} - ${item.availableStock}/${item.totalStock} - ${formatMoney(item.rentalPrice)}
            </option>
        `).join("")}
    `;
    rentalTransactionFields.rentalItemId.value = currentValue;
    updateRentalTransactionPricePreview();
}

function populateRentalBookingPlayerOptions() {
    const currentValue = rentalTransactionFields.bookingPlayerId.value;
    const source = getBookingPlayers(selectedBookingId);
    rentalTransactionFields.bookingPlayerId.innerHTML = `
        <option value="">Selecione um jogador do booking</option>
        ${source.map((bookingPlayer) => `
            <option value="${bookingPlayer.id}">
                #${bookingPlayer.id} - ${escapeHtml(getPlayerName(bookingPlayer.playerId))}
            </option>
        `).join("")}
    `;

    if (source.some((bookingPlayer) => String(bookingPlayer.id) === String(currentValue))) {
        rentalTransactionFields.bookingPlayerId.value = currentValue;
        return;
    }

    rentalTransactionFields.bookingPlayerId.value = "";
}

function populatePaymentBookingPlayerOptions() {
    const currentValue = paymentFields.bookingPlayerId.value;
    const source = getBookingPlayers(selectedBookingId);
    paymentFields.bookingPlayerId.innerHTML = `
        <option value="">Selecione um jogador do booking</option>
        ${source.map((bookingPlayer) => `
            <option value="${bookingPlayer.id}">
                #${bookingPlayer.id} - ${escapeHtml(getPlayerName(bookingPlayer.playerId))} - pendente ${formatMoney(getBookingPlayerPendingAmount(bookingPlayer.id))}
            </option>
        `).join("")}
    `;

    if (source.some((bookingPlayer) => String(bookingPlayer.id) === String(currentValue))) {
        paymentFields.bookingPlayerId.value = currentValue;
        updatePaymentAmountPreview();
        return;
    }

    paymentFields.bookingPlayerId.value = "";
    updatePaymentAmountPreview({ force: true });
}

async function handleRentalItemSubmit(event) {
    event.preventDefault();

    const payload = getRentalItemFormData();
    const isEditing = Boolean(payload.id);
    const method = isEditing ? "PUT" : "POST";
    showRequest(method, RENTAL_ITEM_API_URL, payload);

    try {
        const response = await fetch(RENTAL_ITEM_API_URL, {
            method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        const savedRentalItem = await response.json();
        showResponse(savedRentalItem);
        resetRentalItemForm();
        await loadRentalItems();
        setRentalItemFeedback(
            isEditing ? "Item de aluguer atualizado com sucesso." : "Item de aluguer cadastrado com sucesso.",
            "success"
        );
    } catch (error) {
        setRentalItemFeedback(error.message, "error");
    }
}

function getRentalItemFormData() {
    return {
        id: rentalItemFields.id.value ? Number(rentalItemFields.id.value) : null,
        name: rentalItemFields.name.value.trim(),
        type: rentalItemFields.type.value.trim(),
        totalStock: Number(rentalItemFields.totalStock.value),
        availableStock: rentalItemFields.availableStock.value ? Number(rentalItemFields.availableStock.value) : null,
        rentalPrice: Number(rentalItemFields.rentalPrice.value || 0),
        active: rentalItemFields.active.checked
    };
}

function renderRentalItems(source = rentalItems) {
    visibleRentalItems = source;
    rentalItemCount.textContent = `${source.length} item${source.length === 1 ? "" : "s"}`;

    if (source.length === 0) {
        rentalItemsTableBody.innerHTML = `
            <tr>
                <td colspan="6" class="empty-state">Nenhum item de aluguer encontrado.</td>
            </tr>
        `;
        return;
    }

    rentalItemsTableBody.innerHTML = source
        .map((item) => `
            <tr>
                <td>
                    <div class="row-main">${escapeHtml(item.name)}</div>
                    <div class="row-sub">ID #${item.id}</div>
                </td>
                <td>${escapeHtml(item.type)}</td>
                <td>${item.availableStock}/${item.totalStock}</td>
                <td>${formatMoney(item.rentalPrice)}</td>
                <td><span class="status-pill">${item.active ? "ACTIVE" : "INACTIVE"}</span></td>
                <td>
                    <div class="table-actions">
                        <button class="action-button edit" type="button" data-rental-item-action="edit" data-id="${item.id}">
                            Editar
                        </button>
                        <button class="action-button delete" type="button" data-rental-item-action="delete" data-id="${item.id}">
                            Excluir
                        </button>
                    </div>
                </td>
            </tr>
        `)
        .join("");

    bindRentalItemRowActions();
}

function bindRentalItemRowActions() {
    document.querySelectorAll("[data-rental-item-action='edit']").forEach((button) => {
        button.addEventListener("click", () => {
            const rentalItemId = Number(button.dataset.id);
            const rentalItem = visibleRentalItems.find((item) => item.id === rentalItemId);

            if (!rentalItem) {
                return;
            }

            fillRentalItemForm(rentalItem);
        });
    });

    document.querySelectorAll("[data-rental-item-action='delete']").forEach((button) => {
        button.addEventListener("click", async () => {
            const rentalItemId = Number(button.dataset.id);
            await deleteRentalItem(rentalItemId);
        });
    });
}

function fillRentalItemForm(rentalItem) {
    rentalItemFields.id.value = rentalItem.id ?? "";
    rentalItemFields.name.value = rentalItem.name ?? "";
    rentalItemFields.type.value = rentalItem.type ?? "";
    rentalItemFields.totalStock.value = rentalItem.totalStock ?? "";
    rentalItemFields.availableStock.value = rentalItem.availableStock ?? "";
    rentalItemFields.rentalPrice.value = rentalItem.rentalPrice ?? "";
    rentalItemFields.active.checked = Boolean(rentalItem.active);
    rentalItemFormTitle.textContent = `Editando item #${rentalItem.id}`;
}

function resetRentalItemForm() {
    rentalItemForm.reset();
    rentalItemFields.id.value = "";
    rentalItemFields.active.checked = true;
    rentalItemFormTitle.textContent = "Novo item";
}

async function deleteRentalItem(id) {
    const confirmed = window.confirm("Deseja realmente excluir este item de aluguer?");

    if (!confirmed) {
        return;
    }

    showRequest("DELETE", `${RENTAL_ITEM_API_URL}/${id}`);

    try {
        const response = await fetch(`${RENTAL_ITEM_API_URL}/${id}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        showResponse({
            status: response.status,
            message: `Item de aluguer ${id} excluido com sucesso.`
        });
        resetRentalItemForm();
        await loadRentalItems();
        setRentalItemFeedback("Item de aluguer excluido com sucesso.", "success");
    } catch (error) {
        setRentalItemFeedback(error.message, "error");
    }
}

async function loadRentalTransactions({ silent = false } = {}) {
    if (!silent) {
        setRentalTransactionFeedback("Carregando alugueres...", "success");
    }

    const response = await fetch(RENTAL_TRANSACTION_API_URL);

    if (!response.ok) {
        throw new Error(await extractErrorMessage(response));
    }

    rentalTransactions = await response.json();
    updateReturnAllRentalsButton();

    if (!silent) {
        showResponse(rentalTransactions);
        renderSelectedRentalTransactions();
        setRentalTransactionFeedback("Alugueres carregados com sucesso.", "success");
    }
}

async function loadPayments({ silent = false } = {}) {
    if (!silent) {
        setPaymentFeedback("Carregando pagamentos...", "success");
    }

    const response = await fetch(PAYMENT_API_URL);

    if (!response.ok) {
        throw new Error(await extractErrorMessage(response));
    }

    payments = await response.json();

    if (!silent) {
        showResponse(payments);
        renderSelectedPayments();
        setPaymentFeedback("Pagamentos carregados com sucesso.", "success");
    }
}

function renderDailyAgenda() {
    const selectedDate = getSelectedAgendaDate();
    agendaDayTitle.textContent = `Agenda de ${formatDate(selectedDate)}`;

    const slots = buildAgendaSlots();
    agendaSlotsGrid.innerHTML = slots
        .map((time) => {
            const teeTime = findTeeTimeByDateAndTime(selectedDate, time);
            const maxPlayers = Number(teeTime?.maxPlayers || 4);
            const bookedPlayers = teeTime ? getTeeTimeBookedPlayers(teeTime) : 0;
            const bookingsForSlot = teeTime ? getBookingsForTeeTime(teeTime.id) : [];
            const primaryBooking = bookingsForSlot[0];
            const state = getAgendaSlotState(teeTime, bookedPlayers, maxPlayers);
            const slotLabel = getAgendaSlotLabel(state);
            const bookingLabel = primaryBooking
                ? `${escapeHtml(primaryBooking.code || `Booking #${primaryBooking.id}`)}`
                : state === "free" ? "Clique para reservar" : "Sem booking";

            return `
                <button
                    class="agenda-slot ${state}${primaryBooking?.id === selectedBookingId ? " selected" : ""}"
                    type="button"
                    data-agenda-time="${time}"
                    ${state === "cancelled" ? "disabled" : ""}
                >
                    <span class="slot-time">${time}</span>
                    <span class="slot-status">${slotLabel}</span>
                    <span class="slot-booking">${bookingLabel}</span>
                    <span class="slot-meta">${bookedPlayers}/${maxPlayers} jogadores</span>
                </button>
            `;
        })
        .join("");

    document.querySelectorAll("[data-agenda-time]").forEach((button) => {
        button.addEventListener("click", async () => {
            await handleAgendaSlotClick(button.dataset.agendaTime);
        });
    });
}

async function handleAgendaSlotClick(time) {
    const playDate = getSelectedAgendaDate();
    let teeTime = findTeeTimeByDateAndTime(playDate, time);

    try {
        setAgendaFeedback(`Preparando horario ${time}...`, "success");

        if (teeTime?.status === "CANCELLED") {
            throw new Error("Este tee time esta cancelado.");
        }

        if (!teeTime) {
            teeTime = await createTeeTimeFromAgenda(playDate, time);
        }

        let booking = getBookingsForTeeTime(teeTime.id)[0];

        if (!booking) {
            booking = await createBookingFromAgenda(teeTime.id);
        }

        selectedBookingId = booking.id;
        await refreshBookingContext();

        const refreshedBooking = bookings.find((item) => item.id === booking.id) || booking;
        fillBookingForm(refreshedBooking);
        selectBooking(refreshedBooking.id);
        bookingPlayerForm.scrollIntoView({ behavior: "smooth", block: "start" });
        setAgendaFeedback(`Horario ${time} selecionado. Agora adicione os jogadores e faca o check-in.`, "success");
    } catch (error) {
        setAgendaFeedback(error.message, "error");
    }
}

async function createTeeTimeFromAgenda(playDate, startTime) {
    return sendJsonRequest("POST", TEE_TIME_API_URL, {
        id: null,
        playDate,
        startTime,
        maxPlayers: 4,
        bookedPlayers: 0,
        status: "AVAILABLE",
        baseGreenFee: 0
    });
}

async function createBookingFromAgenda(teeTimeId) {
    return sendJsonRequest("POST", BOOKING_API_URL, {
        id: null,
        code: null,
        status: "CREATED",
        totalAmount: 0,
        createdBy: null,
        teeTimeId
    });
}

async function sendJsonRequest(method, url, payload) {
    showRequest(method, url, payload);

    const response = await fetch(url, {
        method,
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    });

    if (!response.ok) {
        const errorMessage = await extractErrorMessage(response);
        showResponse({
            status: response.status,
            error: errorMessage
        });
        throw new Error(errorMessage);
    }

    const data = await response.json();
    showResponse(data);
    return data;
}

function buildAgendaSlots() {
    const slots = [];

    for (let totalMinutes = 7 * 60; totalMinutes <= 19 * 60; totalMinutes += 10) {
        const hours = String(Math.floor(totalMinutes / 60)).padStart(2, "0");
        const minutes = String(totalMinutes % 60).padStart(2, "0");
        slots.push(`${hours}:${minutes}`);
    }

    return slots;
}

function getSelectedAgendaDate() {
    if (!agendaDateInput.value) {
        agendaDateInput.value = todayIsoDate();
    }

    return agendaDateInput.value;
}

function todayIsoDate() {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");

    return `${year}-${month}-${day}`;
}

function findTeeTimeByDateAndTime(playDate, startTime) {
    return teeTimes.find((teeTime) => {
        return teeTime.playDate === playDate && formatTime(teeTime.startTime) === startTime;
    });
}

function getBookingsForTeeTime(teeTimeId) {
    return bookings.filter((booking) => booking.teeTimeId === teeTimeId && booking.status !== "CANCELLED");
}

function getTeeTimeBookedPlayers(teeTime) {
    const playersFromBookings = getBookingsForTeeTime(teeTime.id)
        .reduce((total, booking) => total + getBookingPlayers(booking.id).length, 0);

    return Math.max(Number(teeTime.bookedPlayers || 0), playersFromBookings);
}

function getAgendaSlotState(teeTime, bookedPlayers, maxPlayers) {
    if (!teeTime) {
        return "free";
    }

    if (teeTime.status === "CANCELLED") {
        return "cancelled";
    }

    if (bookedPlayers >= maxPlayers) {
        return "full";
    }

    if (bookedPlayers > 0) {
        return "partial";
    }

    return "open";
}

function getAgendaSlotLabel(state) {
    const labels = {
        free: "Livre",
        open: "Aberto",
        partial: "Reservado",
        full: "Completo",
        cancelled: "Cancelado"
    };

    return labels[state] || "Livre";
}

async function handleBookingPlayerSubmit(event) {
    event.preventDefault();

    const payload = getBookingPlayerFormData();
    const isEditing = Boolean(payload.id);
    const method = isEditing ? "PUT" : "POST";
    showRequest(method, BOOKING_PLAYER_API_URL, payload);

    try {
        const response = await fetch(BOOKING_PLAYER_API_URL, {
            method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        const savedBookingPlayer = await response.json();
        showResponse(savedBookingPlayer);
        selectedBookingId = savedBookingPlayer.bookingId;
        resetBookingPlayerForm({ keepBooking: true });
        await loadBookings();
        await loadTeeTimesForSelect();
        renderTeeTimes();
        renderDailyAgenda();
        populateRentalBookingPlayerOptions();
        populatePaymentBookingPlayerOptions();
        setBookingPlayerFeedback(
            isEditing ? "Jogador atualizado com sucesso." : "Jogador adicionado com sucesso.",
            "success"
        );
    } catch (error) {
        setBookingPlayerFeedback(error.message, "error");
    }
}

function getBookingPlayerFormData() {
    return {
        id: bookingPlayerFields.id.value ? Number(bookingPlayerFields.id.value) : null,
        bookingId: Number(bookingPlayerFields.bookingId.value),
        playerId: Number(bookingPlayerFields.playerId.value),
        greenFeeAmount: bookingPlayerFields.greenFeeAmount.value
            ? Number(bookingPlayerFields.greenFeeAmount.value)
            : null,
        checkedIn: bookingPlayerFields.checkedIn.checked
    };
}

async function handleRentalTransactionSubmit(event) {
    event.preventDefault();

    const payload = getRentalTransactionFormData();
    const isEditing = Boolean(payload.id);
    const method = isEditing ? "PUT" : "POST";
    showRequest(method, RENTAL_TRANSACTION_API_URL, payload);

    try {
        const response = await fetch(RENTAL_TRANSACTION_API_URL, {
            method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        const savedRentalTransaction = await response.json();
        showResponse(savedRentalTransaction);
        selectedBookingId = savedRentalTransaction.bookingId;
        resetRentalTransactionForm({ keepBooking: true });
        await loadRentalItemsForSelect();
        await loadBookings();
        renderRentalItems();
        setRentalTransactionFeedback(
            isEditing ? "Aluguer atualizado com sucesso." : "Aluguer adicionado com sucesso.",
            "success"
        );
    } catch (error) {
        setRentalTransactionFeedback(error.message, "error");
    }
}

function getRentalTransactionFormData() {
    return {
        id: rentalTransactionFields.id.value ? Number(rentalTransactionFields.id.value) : null,
        bookingId: rentalTransactionFields.bookingId.value ? Number(rentalTransactionFields.bookingId.value) : null,
        bookingPlayerId: rentalTransactionFields.bookingPlayerId.value ? Number(rentalTransactionFields.bookingPlayerId.value) : null,
        rentalItemId: rentalTransactionFields.rentalItemId.value ? Number(rentalTransactionFields.rentalItemId.value) : null,
        quantity: Number(rentalTransactionFields.quantity.value || 1),
        status: rentalTransactionFields.status.value,
        unitPrice: rentalTransactionFields.unitPrice.value ? Number(rentalTransactionFields.unitPrice.value) : null,
        totalPrice: rentalTransactionFields.totalPrice.value ? Number(rentalTransactionFields.totalPrice.value) : null
    };
}

async function handlePaymentSubmit(event) {
    event.preventDefault();

    const payload = getPaymentFormData();
    const isEditing = Boolean(payload.id);
    const method = isEditing ? "PUT" : "POST";
    showRequest(method, PAYMENT_API_URL, payload);

    try {
        const response = await fetch(PAYMENT_API_URL, {
            method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        const savedPayment = await response.json();
        showResponse(savedPayment);
        selectedBookingId = savedPayment.bookingId;
        resetPaymentForm({ keepBooking: true });
        await loadPayments({ silent: true });
        renderSelectedBookingPlayers();
        renderSelectedPayments();
        setPaymentFeedback(
            isEditing ? "Pagamento atualizado com sucesso." : "Pagamento registrado com sucesso.",
            "success"
        );
    } catch (error) {
        setPaymentFeedback(error.message, "error");
    }
}

function getPaymentFormData() {
    return {
        id: paymentFields.id.value ? Number(paymentFields.id.value) : null,
        bookingId: paymentFields.bookingId.value ? Number(paymentFields.bookingId.value) : null,
        bookingPlayerId: paymentFields.bookingPlayerId.value ? Number(paymentFields.bookingPlayerId.value) : null,
        amount: paymentFields.amount.value ? Number(paymentFields.amount.value) : null,
        method: paymentFields.method.value,
        status: paymentFields.status.value,
        paidAt: null
    };
}

function renderSelectedRentalTransactions() {
    const booking = bookings.find((item) => item.id === selectedBookingId)
        || visibleBookings.find((item) => item.id === selectedBookingId);
    const source = getRentalTransactions(selectedBookingId);

    if (!selectedBookingId || !booking) {
        selectedRentalTitle.textContent = "Nenhum booking selecionado";
        rentalTransactionCount.textContent = "0 itens";
        updateReturnAllRentalsButton();
        rentalTransactionsTableBody.innerHTML = `
            <tr>
                <td colspan="7" class="empty-state">Selecione um booking para ver os alugueres.</td>
            </tr>
        `;
        return;
    }

    selectedRentalTitle.textContent = `${booking.code || "Booking"} #${booking.id}`;
    rentalTransactionCount.textContent = `${source.length} item${source.length === 1 ? "" : "s"}`;
    rentalTransactionFields.bookingId.value = booking.id;
    populateRentalBookingPlayerOptions();
    updateReturnAllRentalsButton();

    if (source.length === 0) {
        rentalTransactionsTableBody.innerHTML = `
            <tr>
                <td colspan="7" class="empty-state">Nenhum item alugado neste booking.</td>
            </tr>
        `;
        return;
    }

    rentalTransactionsTableBody.innerHTML = source
        .map((rentalTransaction) => `
            <tr>
                <td>
                    <div class="row-main">${escapeHtml(getBookingPlayerDisplayName(rentalTransaction.bookingPlayerId))}</div>
                    <div class="row-sub">Booking player #${rentalTransaction.bookingPlayerId}</div>
                </td>
                <td>
                    <div class="row-main">${escapeHtml(getRentalItemName(rentalTransaction.rentalItemId))}</div>
                    <div class="row-sub">Item #${rentalTransaction.rentalItemId}</div>
                </td>
                <td>${rentalTransaction.quantity}</td>
                <td><span class="status-pill">${escapeHtml(rentalTransaction.status || "RENTED")}</span></td>
                <td>${formatMoney(rentalTransaction.unitPrice)}</td>
                <td>${formatMoney(rentalTransaction.totalPrice)}</td>
                <td>
                    <div class="table-actions">
                        ${(rentalTransaction.status || "RENTED") === "RENTED" ? `
                            <button class="action-button checkin" type="button" data-rental-transaction-action="return" data-id="${rentalTransaction.id}">
                                Devolver
                            </button>
                        ` : ""}
                        <button class="action-button edit" type="button" data-rental-transaction-action="edit" data-id="${rentalTransaction.id}">
                            Editar
                        </button>
                        <button class="action-button delete" type="button" data-rental-transaction-action="delete" data-id="${rentalTransaction.id}">
                            Excluir
                        </button>
                    </div>
                </td>
            </tr>
        `)
        .join("");

    bindRentalTransactionRowActions();
}

function bindRentalTransactionRowActions() {
    document.querySelectorAll("[data-rental-transaction-action='edit']").forEach((button) => {
        button.addEventListener("click", () => {
            const rentalTransactionId = Number(button.dataset.id);
            const rentalTransaction = rentalTransactions.find((item) => item.id === rentalTransactionId);

            if (!rentalTransaction) {
                return;
            }

            fillRentalTransactionForm(rentalTransaction);
        });
    });

    document.querySelectorAll("[data-rental-transaction-action='delete']").forEach((button) => {
        button.addEventListener("click", async () => {
            const rentalTransactionId = Number(button.dataset.id);
            await deleteRentalTransaction(rentalTransactionId);
        });
    });

    document.querySelectorAll("[data-rental-transaction-action='return']").forEach((button) => {
        button.addEventListener("click", async () => {
            const rentalTransactionId = Number(button.dataset.id);
            await returnRentalTransaction(rentalTransactionId);
        });
    });
}

function renderSelectedPayments() {
    const booking = bookings.find((item) => item.id === selectedBookingId)
        || visibleBookings.find((item) => item.id === selectedBookingId);
    const source = getPayments(selectedBookingId);

    if (!selectedBookingId || !booking) {
        selectedPaymentTitle.textContent = "Nenhum booking selecionado";
        paymentCount.textContent = "0 pagamentos";
        paymentsTableBody.innerHTML = `
            <tr>
                <td colspan="6" class="empty-state">Selecione um booking para ver os pagamentos.</td>
            </tr>
        `;
        return;
    }

    selectedPaymentTitle.textContent = `${booking.code || "Booking"} #${booking.id}`;
    paymentCount.textContent = `${source.length} pagamento${source.length === 1 ? "" : "s"}`;
    paymentFields.bookingId.value = booking.id;
    populatePaymentBookingPlayerOptions();

    if (source.length === 0) {
        paymentsTableBody.innerHTML = `
            <tr>
                <td colspan="6" class="empty-state">Nenhum pagamento registrado neste booking.</td>
            </tr>
        `;
        return;
    }

    paymentsTableBody.innerHTML = source
        .map((payment) => `
            <tr>
                <td>
                    <div class="row-main">${escapeHtml(getBookingPlayerDisplayName(payment.bookingPlayerId))}</div>
                    <div class="row-sub">Booking player #${payment.bookingPlayerId}</div>
                </td>
                <td>
                    <div class="row-main">${formatMoney(payment.amount)}</div>
                    <div class="row-sub">Pendente: ${formatMoney(getBookingPlayerPendingAmount(payment.bookingPlayerId))}</div>
                </td>
                <td>${escapeHtml(payment.method || "-")}</td>
                <td><span class="status-pill">${escapeHtml(payment.status || "PAID")}</span></td>
                <td>${formatDateTime(payment.paidAt) || "-"}</td>
                <td>
                    <div class="table-actions">
                        ${(payment.status || "PAID") === "PAID" ? `
                            <button class="action-button checkin" type="button" data-payment-action="refund" data-id="${payment.id}">
                                Reembolsar
                            </button>
                        ` : ""}
                        <button class="action-button edit" type="button" data-payment-action="edit" data-id="${payment.id}">
                            Editar
                        </button>
                        <button class="action-button delete" type="button" data-payment-action="delete" data-id="${payment.id}">
                            Excluir
                        </button>
                    </div>
                </td>
            </tr>
        `)
        .join("");

    bindPaymentRowActions();
}

function bindPaymentRowActions() {
    document.querySelectorAll("[data-payment-action='edit']").forEach((button) => {
        button.addEventListener("click", () => {
            const paymentId = Number(button.dataset.id);
            const payment = payments.find((item) => item.id === paymentId);

            if (!payment) {
                return;
            }

            fillPaymentForm(payment);
        });
    });

    document.querySelectorAll("[data-payment-action='delete']").forEach((button) => {
        button.addEventListener("click", async () => {
            const paymentId = Number(button.dataset.id);
            await deletePayment(paymentId);
        });
    });

    document.querySelectorAll("[data-payment-action='refund']").forEach((button) => {
        button.addEventListener("click", async () => {
            const paymentId = Number(button.dataset.id);
            await refundPayment(paymentId);
        });
    });
}

function fillRentalTransactionForm(rentalTransaction) {
    rentalTransactionFields.id.value = rentalTransaction.id ?? "";
    rentalTransactionFields.bookingId.value = rentalTransaction.bookingId ?? "";
    populateRentalBookingPlayerOptions();
    rentalTransactionFields.bookingPlayerId.value = rentalTransaction.bookingPlayerId ?? "";
    rentalTransactionFields.rentalItemId.value = rentalTransaction.rentalItemId ?? "";
    rentalTransactionFields.quantity.value = rentalTransaction.quantity ?? 1;
    rentalTransactionFields.status.value = rentalTransaction.status ?? "RENTED";
    rentalTransactionFields.unitPrice.value = rentalTransaction.unitPrice ?? "";
    rentalTransactionFields.totalPrice.value = rentalTransaction.totalPrice ?? "";
    rentalTransactionFormTitle.textContent = `Editando aluguer #${rentalTransaction.id}`;
}

function resetRentalTransactionForm({ keepBooking = false } = {}) {
    const bookingId = keepBooking ? rentalTransactionFields.bookingId.value : "";
    rentalTransactionForm.reset();
    rentalTransactionFields.id.value = "";
    rentalTransactionFields.bookingId.value = bookingId;
    rentalTransactionFields.bookingPlayerId.value = "";
    rentalTransactionFields.quantity.value = 1;
    rentalTransactionFields.status.value = "RENTED";
    rentalTransactionFields.unitPrice.value = "";
    rentalTransactionFields.totalPrice.value = "";
    rentalTransactionFormTitle.textContent = "Adicionar item";
}

function fillPaymentForm(payment) {
    paymentFields.id.value = payment.id ?? "";
    paymentFields.bookingId.value = payment.bookingId ?? "";
    populatePaymentBookingPlayerOptions();
    paymentFields.bookingPlayerId.value = payment.bookingPlayerId ?? "";
    paymentFields.amount.value = payment.amount ?? "";
    paymentFields.method.value = payment.method ?? "CARD";
    paymentFields.status.value = payment.status ?? "PAID";
    paymentFields.paidAt.value = formatDateTime(payment.paidAt);
    paymentFormTitle.textContent = `Editando pagamento #${payment.id}`;
}

function resetPaymentForm({ keepBooking = false } = {}) {
    const bookingId = keepBooking ? paymentFields.bookingId.value : "";
    paymentForm.reset();
    paymentFields.id.value = "";
    paymentFields.bookingId.value = bookingId;
    paymentFields.bookingPlayerId.value = "";
    paymentFields.method.value = "CARD";
    paymentFields.status.value = "PAID";
    paymentFields.amount.value = "";
    paymentFields.paidAt.value = "";
    paymentFormTitle.textContent = "Registrar pagamento";
}

function preparePaymentForBookingPlayer(bookingPlayerId) {
    const bookingPlayer = bookingPlayers.find((item) => item.id === bookingPlayerId);

    if (!bookingPlayer) {
        setPaymentFeedback("Jogador do booking nao encontrado.", "error");
        return;
    }

    paymentFields.id.value = "";
    paymentFields.bookingId.value = bookingPlayer.bookingId ?? "";
    paymentFields.bookingPlayerId.value = bookingPlayer.id ?? "";
    paymentFields.method.value = "CARD";
    paymentFields.status.value = "PAID";
    paymentFields.paidAt.value = "";
    updatePaymentAmountPreview({ force: true });
    paymentFormTitle.textContent = `Pagamento de ${getPlayerName(bookingPlayer.playerId)}`;
    paymentForm.scrollIntoView({ behavior: "smooth", block: "start" });
}

function updatePaymentAmountPreview({ force = false } = {}) {
    const bookingPlayerId = Number(paymentFields.bookingPlayerId.value);
    const ignoredPaymentId = paymentFields.id.value ? Number(paymentFields.id.value) : null;

    if (!bookingPlayerId) {
        if (force) {
            paymentFields.amount.value = "";
        }
        return;
    }

    if (force || !paymentFields.amount.value) {
        paymentFields.amount.value = getBookingPlayerPendingAmount(bookingPlayerId, ignoredPaymentId).toFixed(2);
    }
}

async function deletePayment(id) {
    const confirmed = window.confirm("Deseja realmente remover este pagamento?");

    if (!confirmed) {
        return;
    }

    showRequest("DELETE", `${PAYMENT_API_URL}/${id}`);

    try {
        const response = await fetch(`${PAYMENT_API_URL}/${id}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        showResponse({
            status: response.status,
            message: `Pagamento ${id} removido com sucesso.`
        });
        resetPaymentForm({ keepBooking: true });
        await loadPayments({ silent: true });
        renderSelectedBookingPlayers();
        renderSelectedPayments();
        setPaymentFeedback("Pagamento removido com sucesso.", "success");
    } catch (error) {
        setPaymentFeedback(error.message, "error");
    }
}

async function refundPayment(id) {
    const payment = payments.find((item) => item.id === id);

    if (!payment) {
        setPaymentFeedback("Pagamento nao encontrado.", "error");
        return;
    }

    const payload = {
        ...payment,
        status: "REFUNDED"
    };

    showRequest("PUT", PAYMENT_API_URL, payload);

    try {
        const response = await fetch(PAYMENT_API_URL, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        const refundedPayment = await response.json();
        showResponse(refundedPayment);
        selectedBookingId = refundedPayment.bookingId;
        await loadPayments({ silent: true });
        renderSelectedBookingPlayers();
        renderSelectedPayments();
        setPaymentFeedback("Pagamento marcado como reembolsado.", "success");
    } catch (error) {
        setPaymentFeedback(error.message, "error");
    }
}

async function deleteRentalTransaction(id) {
    const confirmed = window.confirm("Deseja realmente remover este aluguer do booking?");

    if (!confirmed) {
        return;
    }

    showRequest("DELETE", `${RENTAL_TRANSACTION_API_URL}/${id}`);

    try {
        const response = await fetch(`${RENTAL_TRANSACTION_API_URL}/${id}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        showResponse({
            status: response.status,
            message: `Aluguer ${id} removido do booking com sucesso.`
        });
        resetRentalTransactionForm({ keepBooking: true });
        await loadRentalItemsForSelect();
        await loadBookings();
        renderRentalItems();
        setRentalTransactionFeedback("Aluguer removido com sucesso.", "success");
    } catch (error) {
        setRentalTransactionFeedback(error.message, "error");
    }
}

async function returnRentalTransaction(id) {
    const rentalTransaction = rentalTransactions.find((item) => item.id === id);

    if (!rentalTransaction) {
        setRentalTransactionFeedback("Aluguer nao encontrado.", "error");
        return;
    }

    const payload = {
        ...rentalTransaction,
        status: "RETURNED"
    };

    showRequest("PUT", RENTAL_TRANSACTION_API_URL, payload);

    try {
        const response = await fetch(RENTAL_TRANSACTION_API_URL, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        const returnedRentalTransaction = await response.json();
        showResponse(returnedRentalTransaction);
        selectedBookingId = returnedRentalTransaction.bookingId;
        await loadRentalItemsForSelect();
        await loadBookings();
        renderRentalItems();
        setRentalTransactionFeedback("Item devolvido e estoque atualizado com sucesso.", "success");
    } catch (error) {
        setRentalTransactionFeedback(error.message, "error");
    }
}

async function returnAllRentalTransactions() {
    const pendingRentals = rentalTransactions
        .filter((rentalTransaction) => (rentalTransaction.status || "RENTED") === "RENTED");

    if (pendingRentals.length === 0) {
        setRentalTransactionFeedback("Nao ha materiais alugados pendentes de devolucao.", "success");
        updateReturnAllRentalsButton();
        return;
    }

    const materialLabel = pendingRentals.length === 1 ? "material" : "materiais";
    const confirmed = window.confirm(`Deseja marcar ${pendingRentals.length} ${materialLabel} como devolvido${pendingRentals.length === 1 ? "" : "s"}?`);

    if (!confirmed) {
        return;
    }

    const url = `${RENTAL_TRANSACTION_API_URL}/return-all`;
    showRequest("PUT", url);

    try {
        const response = await fetch(url, {
            method: "PUT"
        });

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        const returnedRentalTransactions = await response.json();
        showResponse(returnedRentalTransactions);
        await loadRentalItemsForSelect();
        await loadBookings();
        renderRentalItems();
        updateReturnAllRentalsButton();
        setRentalTransactionFeedback("Todos os materiais pendentes foram marcados como devolvidos.", "success");
    } catch (error) {
        setRentalTransactionFeedback(error.message, "error");
    }
}

function updateReturnAllRentalsButton() {
    const pendingRentals = rentalTransactions
        .filter((rentalTransaction) => (rentalTransaction.status || "RENTED") === "RENTED");

    returnAllRentalsButton.disabled = pendingRentals.length === 0;
    returnAllRentalsButton.textContent = pendingRentals.length === 0
        ? "Todos os materiais devolvidos"
        : `Devolver todos os materiais (${pendingRentals.length})`;
}

function updateRentalTransactionPricePreview() {
    const rentalItemId = Number(rentalTransactionFields.rentalItemId.value);
    const bookingId = Number(rentalTransactionFields.bookingId.value);
    const bookingPlayerId = Number(rentalTransactionFields.bookingPlayerId.value);
    const quantity = Number(rentalTransactionFields.quantity.value || 0);
    const rentalItem = rentalItems.find((item) => item.id === rentalItemId);

    if (!rentalItem || quantity < 1) {
        rentalTransactionFields.unitPrice.value = "";
        rentalTransactionFields.totalPrice.value = "";
        return;
    }

    const booking = bookings.find((item) => item.id === bookingId);
    const bookingPlayer = bookingPlayers.find((item) => item.id === bookingPlayerId);
    const unitPrice = calculateRentalUnitPricePreview(rentalItem, booking, bookingPlayer);
    rentalTransactionFields.unitPrice.value = unitPrice.toFixed(2);
    rentalTransactionFields.totalPrice.value = (unitPrice * quantity).toFixed(2);
}

function calculateRentalUnitPricePreview(rentalItem, booking, bookingPlayer) {
    let unitPrice = Number(rentalItem.rentalPrice || 0);

    if (!isBuggyOrElectricTrolley(rentalItem)) {
        return unitPrice;
    }

    if (isTwilightBooking(booking)) {
        unitPrice *= 0.60;
    }

    if (isMemberBookingPlayer(bookingPlayer)) {
        unitPrice *= 0.50;
    }

    return unitPrice;
}

function isBuggyOrElectricTrolley(rentalItem) {
    const searchableText = normalizeSearchText(`${rentalItem.name || ""} ${rentalItem.type || ""}`).toUpperCase();

    return searchableText.includes("BUGGY")
        || (searchableText.includes("TROLLEY")
            && (searchableText.includes("ELECTRIC") || searchableText.includes("ELETRICO")));
}

function isTwilightBooking(booking) {
    if (!booking) {
        return false;
    }

    const teeTime = teeTimes.find((item) => item.id === booking.teeTimeId);

    return Boolean(teeTime) && formatTime(teeTime.startTime) >= "16:00";
}

function isMemberBookingPlayer(bookingPlayer) {
    if (!bookingPlayer) {
        return false;
    }

    const player = players.find((item) => item.id === bookingPlayer.playerId);

    return Boolean(player?.member);
}

function selectBooking(bookingId) {
    selectedBookingId = bookingId;
    bookingPlayerFields.bookingId.value = bookingId ?? "";
    rentalTransactionFields.bookingId.value = bookingId ?? "";
    paymentFields.bookingId.value = bookingId ?? "";
    populateRentalBookingPlayerOptions();
    populatePaymentBookingPlayerOptions();
    renderSelectedBookingPlayers();
    renderSelectedRentalTransactions();
    renderSelectedPayments();
    renderDailyAgenda();
}

function renderSelectedBookingPlayers() {
    const booking = bookings.find((item) => item.id === selectedBookingId)
        || visibleBookings.find((item) => item.id === selectedBookingId);
    const source = getBookingPlayers(selectedBookingId);

    if (!selectedBookingId || !booking) {
        selectedBookingTitle.textContent = "Nenhum booking selecionado";
        bookingPlayerCount.textContent = "0 jogadores";
        bookingsTableBody.querySelectorAll("tr").forEach((row) => row.classList.remove("selected-row"));
        bookingPlayersTableBody.innerHTML = `
            <tr>
                <td colspan="4" class="empty-state">Selecione um booking para ver os jogadores.</td>
            </tr>
        `;
        return;
    }

    selectedBookingTitle.textContent = `${booking.code || "Booking"} #${booking.id}`;
    bookingPlayerCount.textContent = `${source.length} jogador${source.length === 1 ? "" : "es"}`;
    bookingPlayerFields.bookingId.value = booking.id;

    document.querySelectorAll("[data-booking-action='select']").forEach((button) => {
        button.closest("tr").classList.toggle("selected-row", Number(button.dataset.id) === booking.id);
    });

    if (source.length === 0) {
        bookingPlayersTableBody.innerHTML = `
            <tr>
                <td colspan="4" class="empty-state">Nenhum jogador adicionado a este booking.</td>
            </tr>
        `;
        return;
    }

    bookingPlayersTableBody.innerHTML = source
        .map((bookingPlayer) => `
            <tr>
                <td>
                    <div class="row-main">${escapeHtml(getPlayerName(bookingPlayer.playerId))}</div>
                    <div class="row-sub">Player #${bookingPlayer.playerId}</div>
                </td>
                <td>
                    <div class="row-main">${formatMoney(bookingPlayer.greenFeeAmount)}</div>
                    <div class="row-sub">Rentals: ${formatMoney(getBookingPlayerRentalTotal(bookingPlayer.id))}</div>
                    <div class="row-sub">Total: ${formatMoney(getBookingPlayerTotal(bookingPlayer))}</div>
                    <div class="row-sub">Pago: ${formatMoney(getBookingPlayerPaidAmount(bookingPlayer.id))}</div>
                    <div class="row-sub">Pendente: ${formatMoney(getBookingPlayerPendingAmount(bookingPlayer.id))}</div>
                </td>
                <td>${bookingPlayer.checkedIn ? "Sim" : "Nao"}</td>
                <td>
                    <div class="table-actions">
                        <button class="action-button select" type="button" data-booking-player-action="payment" data-id="${bookingPlayer.id}">
                            Pagar
                        </button>
                        <button class="action-button checkin" type="button" data-booking-player-action="toggle-checkin" data-id="${bookingPlayer.id}">
                            ${bookingPlayer.checkedIn ? "Desfazer check-in" : "Check-in"}
                        </button>
                        <button class="action-button edit" type="button" data-booking-player-action="edit" data-id="${bookingPlayer.id}">
                            Editar
                        </button>
                        <button class="action-button delete" type="button" data-booking-player-action="delete" data-id="${bookingPlayer.id}">
                            Excluir
                        </button>
                    </div>
                </td>
            </tr>
        `)
        .join("");

    bindBookingPlayerRowActions();
}

function bindBookingPlayerRowActions() {
    document.querySelectorAll("[data-booking-player-action='edit']").forEach((button) => {
        button.addEventListener("click", () => {
            const bookingPlayerId = Number(button.dataset.id);
            const bookingPlayer = bookingPlayers.find((item) => item.id === bookingPlayerId);

            if (!bookingPlayer) {
                return;
            }

            fillBookingPlayerForm(bookingPlayer);
        });
    });

    document.querySelectorAll("[data-booking-player-action='delete']").forEach((button) => {
        button.addEventListener("click", async () => {
            const bookingPlayerId = Number(button.dataset.id);
            await deleteBookingPlayer(bookingPlayerId);
        });
    });

    document.querySelectorAll("[data-booking-player-action='toggle-checkin']").forEach((button) => {
        button.addEventListener("click", async () => {
            const bookingPlayerId = Number(button.dataset.id);
            await toggleBookingPlayerCheckIn(bookingPlayerId);
        });
    });

    document.querySelectorAll("[data-booking-player-action='payment']").forEach((button) => {
        button.addEventListener("click", () => {
            preparePaymentForBookingPlayer(Number(button.dataset.id));
        });
    });
}

function fillBookingPlayerForm(bookingPlayer) {
    bookingPlayerFields.id.value = bookingPlayer.id ?? "";
    bookingPlayerFields.bookingId.value = bookingPlayer.bookingId ?? "";
    bookingPlayerFields.playerId.value = bookingPlayer.playerId ?? "";
    bookingPlayerFields.greenFeeAmount.value = bookingPlayer.greenFeeAmount ?? "";
    bookingPlayerFields.checkedIn.checked = Boolean(bookingPlayer.checkedIn);
    bookingPlayerFormTitle.textContent = `Editando jogador #${bookingPlayer.id}`;
}

function resetBookingPlayerForm({ keepBooking = false } = {}) {
    const bookingId = keepBooking ? bookingPlayerFields.bookingId.value : "";
    bookingPlayerForm.reset();
    bookingPlayerFields.id.value = "";
    bookingPlayerFields.bookingId.value = bookingId;
    bookingPlayerFormTitle.textContent = "Adicionar jogador";
}

async function deleteBookingPlayer(id) {
    const confirmed = window.confirm("Deseja realmente remover este jogador do booking?");

    if (!confirmed) {
        return;
    }

    showRequest("DELETE", `${BOOKING_PLAYER_API_URL}/${id}`);

    try {
        const response = await fetch(`${BOOKING_PLAYER_API_URL}/${id}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        showResponse({
            status: response.status,
            message: `Jogador ${id} removido do booking com sucesso.`
        });
        resetBookingPlayerForm({ keepBooking: true });
        await loadBookings();
        await loadTeeTimesForSelect();
        renderTeeTimes();
        renderDailyAgenda();
        populateRentalBookingPlayerOptions();
        populatePaymentBookingPlayerOptions();
        setBookingPlayerFeedback("Jogador removido com sucesso.", "success");
    } catch (error) {
        setBookingPlayerFeedback(error.message, "error");
    }
}

async function toggleBookingPlayerCheckIn(id) {
    const bookingPlayer = bookingPlayers.find((item) => item.id === id);

    if (!bookingPlayer) {
        setBookingPlayerFeedback("Jogador do booking nao encontrado.", "error");
        return;
    }

    const payload = {
        ...bookingPlayer,
        checkedIn: !bookingPlayer.checkedIn
    };

    showRequest("PUT", BOOKING_PLAYER_API_URL, payload);

    try {
        const response = await fetch(BOOKING_PLAYER_API_URL, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            showResponse({
                status: response.status,
                error: errorMessage
            });
            throw new Error(errorMessage);
        }

        const savedBookingPlayer = await response.json();
        showResponse(savedBookingPlayer);
        selectedBookingId = savedBookingPlayer.bookingId;
        await loadBookings();
        await loadTeeTimesForSelect();
        renderTeeTimes();
        renderDailyAgenda();
        populateRentalBookingPlayerOptions();
        populatePaymentBookingPlayerOptions();
        setBookingPlayerFeedback("Check-in atualizado com sucesso.", "success");
    } catch (error) {
        setBookingPlayerFeedback(error.message, "error");
    }
}

function getBookingPlayers(bookingId) {
    if (!bookingId) {
        return [];
    }

    return bookingPlayers.filter((bookingPlayer) => bookingPlayer.bookingId === bookingId);
}

function getRentalTransactions(bookingId) {
    if (!bookingId) {
        return [];
    }

    return rentalTransactions.filter((rentalTransaction) => rentalTransaction.bookingId === bookingId);
}

function getRentalTransactionsByBookingPlayer(bookingPlayerId) {
    if (!bookingPlayerId) {
        return [];
    }

    return rentalTransactions.filter((rentalTransaction) => rentalTransaction.bookingPlayerId === bookingPlayerId);
}

function getPayments(bookingId) {
    if (!bookingId) {
        return [];
    }

    return payments.filter((payment) => payment.bookingId === bookingId);
}

function getPaymentsByBookingPlayer(bookingPlayerId) {
    if (!bookingPlayerId) {
        return [];
    }

    return payments.filter((payment) => payment.bookingPlayerId === bookingPlayerId);
}

async function extractErrorMessage(response) {
    try {
        const body = await response.json();
        return body.message || "Nao foi possivel concluir a operacao.";
    } catch {
        return "Nao foi possivel concluir a operacao.";
    }
}

function setFeedback(message, type = "") {
    feedback.textContent = message;
    feedback.className = `feedback ${type}`.trim();
}

function setTeeTimeFeedback(message, type = "") {
    teeTimeFeedback.textContent = message;
    teeTimeFeedback.className = `feedback ${type}`.trim();
}

function setBookingFeedback(message, type = "") {
    bookingFeedback.textContent = message;
    bookingFeedback.className = `feedback ${type}`.trim();
}

function setBookingPlayerFeedback(message, type = "") {
    bookingPlayerFeedback.textContent = message;
    bookingPlayerFeedback.className = `feedback ${type}`.trim();
}

function setRentalTransactionFeedback(message, type = "") {
    rentalTransactionFeedback.textContent = message;
    rentalTransactionFeedback.className = `feedback ${type}`.trim();
}

function setPaymentFeedback(message, type = "") {
    paymentFeedback.textContent = message;
    paymentFeedback.className = `feedback ${type}`.trim();
}

function setRentalItemFeedback(message, type = "") {
    rentalItemFeedback.textContent = message;
    rentalItemFeedback.className = `feedback ${type}`.trim();
}

function setAgendaFeedback(message, type = "") {
    agendaFeedback.textContent = message;
    agendaFeedback.className = `feedback ${type}`.trim();
}

function showRequest(method, url, body = null) {
    requestJson.textContent = JSON.stringify(
        {
            method,
            url,
            body
        },
        null,
        2
    );
}

function showResponse(data) {
    responseJson.textContent = JSON.stringify(data, null, 2);
}

function clearJsonResponse() {
    responseJson.textContent = "Nenhuma resposta recebida ainda.";
}

function sortTeeTimes(source) {
    return [...source].sort((first, second) => {
        const firstValue = `${first.playDate}T${formatTime(first.startTime)}`;
        const secondValue = `${second.playDate}T${formatTime(second.startTime)}`;
        return firstValue.localeCompare(secondValue);
    });
}

function sortBookings(source) {
    return [...source].sort((first, second) => {
        const firstValue = first.createdAt || "";
        const secondValue = second.createdAt || "";
        return secondValue.localeCompare(firstValue);
    });
}

function formatDate(value) {
    if (!value) {
        return "-";
    }

    const [year, month, day] = String(value).split("-");

    if (!year || !month || !day) {
        return value;
    }

    return `${day}/${month}/${year}`;
}

function formatDateTime(value) {
    if (!value) {
        return "";
    }

    const [date, time = ""] = String(value).split("T");
    const formattedDate = formatDate(date);
    const formattedTime = time.slice(0, 5);

    if (!formattedTime) {
        return formattedDate;
    }

    return `${formattedDate} ${formattedTime}`;
}

function formatTime(value) {
    if (!value) {
        return "";
    }

    return String(value).slice(0, 5);
}

function formatMoney(value) {
    return new Intl.NumberFormat("pt-PT", {
        style: "currency",
        currency: "EUR"
    }).format(Number(value || 0));
}

function getPlayerName(playerId) {
    const player = players.find((item) => item.id === playerId);
    return player ? player.fullName : `Player #${playerId}`;
}

function getRentalItemName(rentalItemId) {
    const rentalItem = rentalItems.find((item) => item.id === rentalItemId);
    return rentalItem ? rentalItem.name : `Item #${rentalItemId}`;
}

function getBookingPlayerDisplayName(bookingPlayerId) {
    const bookingPlayer = bookingPlayers.find((item) => item.id === bookingPlayerId);
    return bookingPlayer ? getPlayerName(bookingPlayer.playerId) : `Booking player #${bookingPlayerId}`;
}

function getBookingPlayerRentalTotal(bookingPlayerId) {
    return getRentalTransactionsByBookingPlayer(bookingPlayerId)
        .filter((rentalTransaction) => (rentalTransaction.status || "RENTED") !== "CANCELLED")
        .reduce((total, rentalTransaction) => total + Number(rentalTransaction.totalPrice || 0), 0);
}

function getBookingPlayerTotal(bookingPlayer) {
    return Number(bookingPlayer.greenFeeAmount || 0) + getBookingPlayerRentalTotal(bookingPlayer.id);
}

function getBookingPlayerPaidAmount(bookingPlayerId, ignoredPaymentId = null) {
    return getPaymentsByBookingPlayer(bookingPlayerId)
        .filter((payment) => (payment.status || "PAID") === "PAID")
        .filter((payment) => ignoredPaymentId === null || payment.id !== ignoredPaymentId)
        .reduce((total, payment) => total + Number(payment.amount || 0), 0);
}

function getBookingPlayerPendingAmount(bookingPlayerId, ignoredPaymentId = null) {
    const bookingPlayer = bookingPlayers.find((item) => item.id === bookingPlayerId);

    if (!bookingPlayer) {
        return 0;
    }

    return Math.max(getBookingPlayerTotal(bookingPlayer) - getBookingPlayerPaidAmount(bookingPlayerId, ignoredPaymentId), 0);
}

function normalizeSearchText(value) {
    return String(value || "")
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .trim()
        .toLowerCase();
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
