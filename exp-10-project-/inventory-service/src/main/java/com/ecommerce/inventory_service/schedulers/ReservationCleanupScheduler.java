package com.ecommerce.inventory_service.schedulers;

import com.ecommerce.inventory_service.entities.Inventory;
import com.ecommerce.inventory_service.entities.InventoryReservation;
import com.ecommerce.inventory_service.entities.enums.Status;
import com.ecommerce.inventory_service.repositories.InventoryRepository;
import com.ecommerce.inventory_service.repositories.InventoryReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationCleanupScheduler {

    private final InventoryReservationRepository reservationRepository;
    private final InventoryRepository inventoryRepository;

    @Scheduled(fixedRate = 60000) // every 1 min
    @SchedulerLock(name = "releaseExpiredReservations", lockAtLeastFor = "PT30S", lockAtMostFor = "PT5M")
    public void releaseExpiredReservations() {

        log.info("START releasing expired reservations");

        List<InventoryReservation> expired =
                reservationRepository.findByStatusAndExpiresAtBefore((
                                Status.RESERVED ),
                                LocalDateTime.now()
                );

        log.info("Found expired reservations count={}", expired.size());

        for (InventoryReservation r : expired) {

            try {
                log.info("Releasing reservation | orderId={} | sku={} | qty={}",
                        r.getOrderId(), r.getSku(), r.getQuantity());

                Inventory inventory = inventoryRepository.findBySku(r.getSku())
                        .orElseThrow(() -> new RuntimeException("Inventory not found"));

                int oldReserved = inventory.getReservedQuantity();

                inventory.setReservedQuantity(
                        oldReserved - r.getQuantity()
                );

                log.info("Inventory updated | sku={} | oldReserved={} | newReserved={}",
                        r.getSku(), oldReserved, inventory.getReservedQuantity());

                r.setStatus(Status.EXPIRED);

                inventoryRepository.save(inventory);
                reservationRepository.save(r);

                log.info("Released reservation for orderId={}", r.getOrderId());

            } catch (Exception e) {
                log.error("Failed to release reservation | orderId={}", r.getOrderId(), e);
            }
        }

        log.info("END releasing expired reservations");
    }
}
