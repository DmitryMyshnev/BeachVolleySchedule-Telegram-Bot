package com.enterprise.myshnev.telegrambot.scheduler.commands;

import com.enterprise.myshnev.telegrambot.scheduler.servises.messages.SendMessageService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.user.UserService;
import com.enterprise.myshnev.telegrambot.scheduler.servises.workout.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import static com.enterprise.myshnev.telegrambot.scheduler.commands.CommandUtils.*;

public class DeleteAllMessageCommand implements Command {
    private final UserService userService;
    private final WorkoutService workoutService;
    private final SendMessageService sendMessageService;
    private final String SUPER_ADMIN;

    @Autowired
    public DeleteAllMessageCommand(UserService userService, WorkoutService workoutService, SendMessageService sendMessageService) {
        this.userService = userService;
        this.workoutService = workoutService;
        this.sendMessageService = sendMessageService;
        SUPER_ADMIN = SuperAdminUtils.getInstance().getIdSuperAdminFromFileConfig();
    }


    @Override
    public void execute(Update update) {
        userService.findByChatId(getChatId(update)).ifPresent(user->{
            if(user.getId().equals(SUPER_ADMIN)){
                workoutService.deleteAllNewWorkout();
             userService.findAllSentMessage().forEach(sentMessage->{
                 boolean ok = sendMessageService.deleteMessage(sentMessage.getUser().getId(), sentMessage.getMessageId());
                 if(ok){
                     userService.deleteSentMessage(sentMessage);
                 }else {
                     String errorMessage = "Error deleting from user: " + sentMessage.getUser().getId();
                     sendMessageService.sendMessage(user.getId(),errorMessage,null);
                 }
             });
             workoutService.findAllWorkout().forEach(workout -> {
                 workout.setActive(false);
                 workoutService.saveWorkout(workout);
             });
             sendMessageService.sendMessage(user.getId(),"All message was deleted",null);
            }
        });
    }
}
