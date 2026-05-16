package com.project.golfofficeapi.services;

import com.project.golfofficeapi.dto.AgendaDayDTO;
import com.project.golfofficeapi.dto.PlayerDTO;
import com.project.golfofficeapi.dto.RentalItemDTO;
import com.project.golfofficeapi.mapper.custom.BookingMapper;
import com.project.golfofficeapi.mapper.custom.BookingPlayerMapper;
import com.project.golfofficeapi.mapper.custom.CheckInTicketMapper;
import com.project.golfofficeapi.mapper.custom.PaymentMapper;
import com.project.golfofficeapi.mapper.custom.ReceiptItemMapper;
import com.project.golfofficeapi.mapper.custom.ReceiptMapper;
import com.project.golfofficeapi.mapper.custom.RentalTransactionMapper;
import com.project.golfofficeapi.mapper.custom.TeeTimeMapper;
import com.project.golfofficeapi.repository.BookingPlayerRepository;
import com.project.golfofficeapi.repository.BookingRepository;
import com.project.golfofficeapi.repository.CheckInTicketRepository;
import com.project.golfofficeapi.repository.PaymentRepository;
import com.project.golfofficeapi.repository.PlayerRepository;
import com.project.golfofficeapi.repository.ReceiptItemRepository;
import com.project.golfofficeapi.repository.ReceiptRepository;
import com.project.golfofficeapi.repository.RentalItemRepository;
import com.project.golfofficeapi.repository.RentalTransactionRepository;
import com.project.golfofficeapi.repository.TeeTimeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.logging.Logger;

import static com.project.golfofficeapi.mapper.ObjectMapper.parseListObject;

@Service
public class AgendaQueryService {

    private final Logger logger = Logger.getLogger(AgendaQueryService.class.getName());

    private final PlayerRepository playerRepository;
    private final RentalItemRepository rentalItemRepository;
    private final TeeTimeRepository teeTimeRepository;
    private final BookingRepository bookingRepository;
    private final BookingPlayerRepository bookingPlayerRepository;
    private final RentalTransactionRepository rentalTransactionRepository;
    private final PaymentRepository paymentRepository;
    private final ReceiptRepository receiptRepository;
    private final ReceiptItemRepository receiptItemRepository;
    private final CheckInTicketRepository checkInTicketRepository;
    private final TeeTimeMapper teeTimeMapper;
    private final BookingMapper bookingMapper;
    private final BookingPlayerMapper bookingPlayerMapper;
    private final RentalTransactionMapper rentalTransactionMapper;
    private final PaymentMapper paymentMapper;
    private final ReceiptMapper receiptMapper;
    private final ReceiptItemMapper receiptItemMapper;
    private final CheckInTicketMapper checkInTicketMapper;

    public AgendaQueryService(
            PlayerRepository playerRepository,
            RentalItemRepository rentalItemRepository,
            TeeTimeRepository teeTimeRepository,
            BookingRepository bookingRepository,
            BookingPlayerRepository bookingPlayerRepository,
            RentalTransactionRepository rentalTransactionRepository,
            PaymentRepository paymentRepository,
            ReceiptRepository receiptRepository,
            ReceiptItemRepository receiptItemRepository,
            CheckInTicketRepository checkInTicketRepository,
            TeeTimeMapper teeTimeMapper,
            BookingMapper bookingMapper,
            BookingPlayerMapper bookingPlayerMapper,
            RentalTransactionMapper rentalTransactionMapper,
            PaymentMapper paymentMapper,
            ReceiptMapper receiptMapper,
            ReceiptItemMapper receiptItemMapper,
            CheckInTicketMapper checkInTicketMapper
    ) {
        this.playerRepository = playerRepository;
        this.rentalItemRepository = rentalItemRepository;
        this.teeTimeRepository = teeTimeRepository;
        this.bookingRepository = bookingRepository;
        this.bookingPlayerRepository = bookingPlayerRepository;
        this.rentalTransactionRepository = rentalTransactionRepository;
        this.paymentRepository = paymentRepository;
        this.receiptRepository = receiptRepository;
        this.receiptItemRepository = receiptItemRepository;
        this.checkInTicketRepository = checkInTicketRepository;
        this.teeTimeMapper = teeTimeMapper;
        this.bookingMapper = bookingMapper;
        this.bookingPlayerMapper = bookingPlayerMapper;
        this.rentalTransactionMapper = rentalTransactionMapper;
        this.paymentMapper = paymentMapper;
        this.receiptMapper = receiptMapper;
        this.receiptItemMapper = receiptItemMapper;
        this.checkInTicketMapper = checkInTicketMapper;
    }

    @Transactional(readOnly = true)
    public AgendaDayDTO findDay(LocalDate date) {
        logger.info("Find Agenda Day");

        AgendaDayDTO dto = new AgendaDayDTO();
        dto.setDate(date);
        dto.setPlayers(parseListObject(playerRepository.findAll(), PlayerDTO.class));
        dto.setRentalItems(parseListObject(rentalItemRepository.findAll(), RentalItemDTO.class));
        dto.setTeeTimes(teeTimeMapper.toDTOList(teeTimeRepository.findByPlayDate(date)));
        dto.setBookings(bookingMapper.toDTOList(bookingRepository.findByTeeTime_PlayDate(date)));
        dto.setBookingPlayers(bookingPlayerMapper.toDTOList(bookingPlayerRepository.findByBooking_TeeTime_PlayDate(date)));
        dto.setRentalTransactions(rentalTransactionMapper.toDTOList(rentalTransactionRepository.findByBooking_TeeTime_PlayDate(date)));
        dto.setPayments(paymentMapper.toDTOList(paymentRepository.findByBooking_TeeTime_PlayDate(date)));
        dto.setReceipts(receiptMapper.toDTOList(receiptRepository.findByPlayDate(date)));
        dto.setReceiptItems(receiptItemMapper.toDTOList(receiptItemRepository.findByReceipt_PlayDate(date)));
        dto.setCheckInTickets(checkInTicketMapper.toDTOList(checkInTicketRepository.findByPlayDate(date)));
        return dto;
    }
}
