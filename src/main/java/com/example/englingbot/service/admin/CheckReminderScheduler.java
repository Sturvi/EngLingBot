package com.example.englingbot.service.admin;

import com.example.englingbot.model.enums.UserRoleEnum;
import com.example.englingbot.service.AppUserService;
import com.example.englingbot.service.message.TelegramMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler component that checks and sends reminders to admins regarding word reviews.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CheckReminderScheduler {

    private final AppUserService appUserService;
    private final WordReviewService wordReviewService;
    private final TelegramMessageService telegramMessageService;

    /**
     * Scheduled method that runs every day at 17:00 Baku time. It checks if there are words
     * pending review and sends reminders to administrators.
     */
    @Scheduled(cron = "0 0 17 * * ?", zone = "Asia/Baku")
    public void runEveryDayAt17HoursBakuTime() {
        log.trace("Starting scheduled task: runEveryDayAt17HoursBakuTime");

        var administratorsList = appUserService.getAppUserListByRole(UserRoleEnum.ADMIN);

        log.debug("Found {} administrators for reminders", administratorsList.size());

        var count = wordReviewService.countOfWordInReview();

        log.debug("Counted {} words awaiting review", count);

        if (count > 0) {
            String messageText = String.format("""
                Напоминание о необходимости проверки слов.

                В настоящий момент ждут проверки %d слов!""", count);

            log.info("Sending reminders to admins about {} words awaiting review", count);

            administratorsList.forEach(admin -> {
                telegramMessageService.sendMessageToAdmin(admin.getTelegramChatId(), messageText);
                log.trace("Reminder sent to admin with Telegram Chat ID: {}", admin.getTelegramChatId());
            });
        } else {
            log.info("No words awaiting review. Skipping reminder.");
        }
    }
}
