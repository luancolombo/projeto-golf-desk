package com.project.golfofficeapi.controllers.assemblers;

import com.project.golfofficeapi.controllers.BookingController;
import com.project.golfofficeapi.controllers.BookingPlayerController;
import com.project.golfofficeapi.controllers.CashRegisterClosureController;
import com.project.golfofficeapi.controllers.CashRegisterClosureItemController;
import com.project.golfofficeapi.controllers.CheckInTicketController;
import com.project.golfofficeapi.controllers.PaymentController;
import com.project.golfofficeapi.controllers.PlayerController;
import com.project.golfofficeapi.controllers.ReceiptController;
import com.project.golfofficeapi.controllers.ReceiptItemController;
import com.project.golfofficeapi.controllers.RentalDamageReportController;
import com.project.golfofficeapi.controllers.RentalItemController;
import com.project.golfofficeapi.controllers.RentalTransactionController;
import com.project.golfofficeapi.controllers.TeeTimeController;
import com.project.golfofficeapi.dto.BookingDTO;
import com.project.golfofficeapi.dto.BookingPlayerDTO;
import com.project.golfofficeapi.dto.CashRegisterClosureDTO;
import com.project.golfofficeapi.dto.CashRegisterClosureItemDTO;
import com.project.golfofficeapi.dto.CheckInTicketDTO;
import com.project.golfofficeapi.dto.PaymentDTO;
import com.project.golfofficeapi.dto.PlayerDTO;
import com.project.golfofficeapi.dto.ReceiptDTO;
import com.project.golfofficeapi.dto.ReceiptItemDTO;
import com.project.golfofficeapi.dto.RentalDamageReportDTO;
import com.project.golfofficeapi.dto.RentalItemDTO;
import com.project.golfofficeapi.dto.RentalTransactionDTO;
import com.project.golfofficeapi.dto.TeeTimeDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ResourceLinkAssembler {

    public List<BookingDTO> bookings(List<BookingDTO> dtos) {
        return addLinks(dtos, this::booking);
    }

    public BookingDTO booking(BookingDTO dto) {
        dto.add(linkTo(methodOn(BookingController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(BookingController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(BookingController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(BookingController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(BookingController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        return dto;
    }

    public List<BookingPlayerDTO> bookingPlayers(List<BookingPlayerDTO> dtos) {
        return addLinks(dtos, this::bookingPlayer);
    }

    public BookingPlayerDTO bookingPlayer(BookingPlayerDTO dto) {
        dto.add(linkTo(methodOn(BookingPlayerController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(BookingPlayerController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(BookingPlayerController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(BookingPlayerController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(BookingPlayerController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        return dto;
    }

    public List<CashRegisterClosureDTO> cashRegisterClosures(List<CashRegisterClosureDTO> dtos) {
        return addLinks(dtos, this::cashRegisterClosure);
    }

    public CashRegisterClosureDTO cashRegisterClosure(CashRegisterClosureDTO dto) {
        if (dto.getId() != null) {
            dto.add(linkTo(methodOn(CashRegisterClosureController.class).findById(dto.getId())).withSelfRel().withType("GET"));
            dto.add(linkTo(methodOn(CashRegisterClosureController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        }
        dto.add(linkTo(methodOn(CashRegisterClosureController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(CashRegisterClosureController.class).findByBusinessDate(dto.getBusinessDate())).withRel("findByBusinessDate").withType("GET"));
        dto.add(linkTo(methodOn(CashRegisterClosureController.class).preview(dto.getBusinessDate())).withRel("preview").withType("GET"));
        dto.add(linkTo(methodOn(CashRegisterClosureController.class).close(dto)).withRel("close").withType("POST"));
        dto.add(linkTo(methodOn(CashRegisterClosureController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(CashRegisterClosureController.class).update(dto)).withRel("update").withType("PUT"));
        if (dto.getItems() != null) {
            dto.getItems().stream()
                    .filter(item -> item.getId() != null)
                    .forEach(this::cashRegisterClosureItemSelf);
        }
        return dto;
    }

    public List<CashRegisterClosureItemDTO> cashRegisterClosureItems(List<CashRegisterClosureItemDTO> dtos) {
        return addLinks(dtos, this::cashRegisterClosureItem);
    }

    public CashRegisterClosureItemDTO cashRegisterClosureItem(CashRegisterClosureItemDTO dto) {
        dto.add(linkTo(methodOn(CashRegisterClosureItemController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(CashRegisterClosureItemController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(CashRegisterClosureItemController.class).findByCashRegisterClosureId(dto.getCashRegisterClosureId())).withRel("findByCashRegisterClosure").withType("GET"));
        dto.add(linkTo(methodOn(CashRegisterClosureItemController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(CashRegisterClosureItemController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(CashRegisterClosureItemController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        return dto;
    }

    public CashRegisterClosureItemDTO cashRegisterClosureItemSelf(CashRegisterClosureItemDTO dto) {
        dto.add(linkTo(methodOn(CashRegisterClosureItemController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        return dto;
    }

    public List<CheckInTicketDTO> checkInTickets(List<CheckInTicketDTO> dtos) {
        return addLinks(dtos, this::checkInTicket);
    }

    public CheckInTicketDTO checkInTicket(CheckInTicketDTO dto) {
        dto.add(linkTo(methodOn(CheckInTicketController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(CheckInTicketController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(CheckInTicketController.class).findByBookingPlayerId(dto.getBookingPlayerId())).withRel("findByBookingPlayer").withType("GET"));
        dto.add(linkTo(methodOn(CheckInTicketController.class).issueByBookingPlayerId(dto.getBookingPlayerId())).withRel("issueByBookingPlayer").withType("POST"));
        dto.add(linkTo(methodOn(CheckInTicketController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(CheckInTicketController.class).cancel(dto.getId(), dto.getCancellationReason())).withRel("cancel").withType("PUT"));
        dto.add(linkTo(methodOn(CheckInTicketController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        return dto;
    }

    public List<PaymentDTO> payments(List<PaymentDTO> dtos) {
        return addLinks(dtos, this::payment);
    }

    public PaymentDTO payment(PaymentDTO dto) {
        dto.add(linkTo(methodOn(PaymentController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(PaymentController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(PaymentController.class).findByBookingId(dto.getBookingId())).withRel("findByBooking").withType("GET"));
        dto.add(linkTo(methodOn(PaymentController.class).findByBookingPlayerId(dto.getBookingPlayerId())).withRel("findByBookingPlayer").withType("GET"));
        dto.add(linkTo(methodOn(PaymentController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(PaymentController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(PaymentController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        return dto;
    }

    public List<PlayerDTO> players(List<PlayerDTO> dtos) {
        return addLinks(dtos, this::player);
    }

    public PlayerDTO player(PlayerDTO dto) {
        dto.add(linkTo(methodOn(PlayerController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(PlayerController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(PlayerController.class).findByName(dto.getFullName())).withRel("findByName").withType("GET"));
        dto.add(linkTo(methodOn(PlayerController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(PlayerController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(PlayerController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        return dto;
    }

    public List<ReceiptDTO> receipts(List<ReceiptDTO> dtos) {
        return addLinks(dtos, this::receipt);
    }

    public ReceiptDTO receipt(ReceiptDTO dto) {
        dto.add(linkTo(methodOn(ReceiptController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(ReceiptController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(ReceiptController.class).findByBookingId(dto.getBookingId())).withRel("findByBooking").withType("GET"));
        dto.add(linkTo(methodOn(ReceiptController.class).findByBookingPlayerId(dto.getBookingPlayerId())).withRel("findByBookingPlayer").withType("GET"));
        dto.add(linkTo(methodOn(ReceiptController.class).findByPaymentId(dto.getPaymentId())).withRel("findByPayment").withType("GET"));
        dto.add(linkTo(methodOn(ReceiptController.class).issueByPaymentId(dto.getPaymentId())).withRel("issueByPayment").withType("POST"));
        dto.add(linkTo(methodOn(ReceiptController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(ReceiptController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(ReceiptController.class).cancel(dto.getId(), dto.getCancellationReason())).withRel("cancel").withType("PUT"));
        dto.add(linkTo(methodOn(ReceiptController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        return dto;
    }

    public List<ReceiptItemDTO> receiptItems(List<ReceiptItemDTO> dtos) {
        return addLinks(dtos, this::receiptItem);
    }

    public ReceiptItemDTO receiptItem(ReceiptItemDTO dto) {
        dto.add(linkTo(methodOn(ReceiptItemController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(ReceiptItemController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(ReceiptItemController.class).findByReceiptId(dto.getReceiptId())).withRel("findByReceipt").withType("GET"));
        dto.add(linkTo(methodOn(ReceiptItemController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(ReceiptItemController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(ReceiptItemController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        return dto;
    }

    public List<RentalDamageReportDTO> rentalDamageReports(List<RentalDamageReportDTO> dtos) {
        return addLinks(dtos, this::rentalDamageReport);
    }

    public RentalDamageReportDTO rentalDamageReport(RentalDamageReportDTO dto) {
        dto.add(linkTo(methodOn(RentalDamageReportController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(RentalDamageReportController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(RentalDamageReportController.class).findByStatus(dto.getStatus())).withRel("findByStatus").withType("GET"));
        dto.add(linkTo(methodOn(RentalDamageReportController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(RentalDamageReportController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(RentalDamageReportController.class).resolve(dto.getId())).withRel("resolve").withType("PUT"));
        dto.add(linkTo(methodOn(RentalDamageReportController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        return dto;
    }

    public List<RentalItemDTO> rentalItems(List<RentalItemDTO> dtos) {
        return addLinks(dtos, this::rentalItem);
    }

    public RentalItemDTO rentalItem(RentalItemDTO dto) {
        dto.add(linkTo(methodOn(RentalItemController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(RentalItemController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(RentalItemController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(RentalItemController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(RentalItemController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        return dto;
    }

    public List<RentalTransactionDTO> rentalTransactions(List<RentalTransactionDTO> dtos) {
        return addLinks(dtos, this::rentalTransaction);
    }

    public RentalTransactionDTO rentalTransaction(RentalTransactionDTO dto) {
        dto.add(linkTo(methodOn(RentalTransactionController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(RentalTransactionController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(RentalTransactionController.class).findByBookingId(dto.getBookingId())).withRel("findByBooking").withType("GET"));
        dto.add(linkTo(methodOn(RentalTransactionController.class).findByBookingPlayerId(dto.getBookingPlayerId())).withRel("findByBookingPlayer").withType("GET"));
        dto.add(linkTo(methodOn(RentalTransactionController.class).returnAllByBookingId(dto.getBookingId())).withRel("returnAllByBooking").withType("PUT"));
        dto.add(linkTo(methodOn(RentalTransactionController.class).returnAll()).withRel("returnAll").withType("PUT"));
        dto.add(linkTo(methodOn(RentalTransactionController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(RentalTransactionController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(RentalTransactionController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        return dto;
    }

    public List<TeeTimeDTO> teeTimes(List<TeeTimeDTO> dtos) {
        return addLinks(dtos, this::teeTime);
    }

    public TeeTimeDTO teeTime(TeeTimeDTO dto) {
        dto.add(linkTo(methodOn(TeeTimeController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(TeeTimeController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(TeeTimeController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(TeeTimeController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(TeeTimeController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        return dto;
    }

    private <T> List<T> addLinks(List<T> dtos, Consumer<T> linkAdder) {
        dtos.forEach(linkAdder);
        return dtos;
    }
}
