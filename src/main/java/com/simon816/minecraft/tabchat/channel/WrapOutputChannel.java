package com.simon816.minecraft.tabchat.channel;

import com.simon816.minecraft.tabchat.PlayerChatView;
import com.simon816.minecraft.tabchat.TabbedChat;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.channel.impl.DelegateMessageChannel;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatTypes;

import java.util.Optional;

import javax.annotation.Nullable;

public class WrapOutputChannel extends DelegateMessageChannel {

    private final CommandSource causeSource;

    public WrapOutputChannel(MessageChannel channel, CommandSource causeSource) {
        super(channel);
        this.causeSource = causeSource;
    }

    @Override
    public Optional<Text> transformMessage(@Nullable Object sender, MessageReceiver recipient, Text original, ChatType type) {
        Optional<Text> optMessage = super.transformMessage(sender, recipient, original, type);
        if (!optMessage.isPresent()) {
            return optMessage;
        }
        if (recipient instanceof Player && type != ChatTypes.ACTION_BAR) {
            PlayerChatView view = TabbedChat.getView((Player) recipient);
            CommandSource source = this.causeSource;
            if (sender instanceof CommandSource) {
                source = (CommandSource) sender;
            }
            if (view != null) {
                return view.transformOutgoing(source, optMessage.get(), type);
            }
        }
        return optMessage;
    }
}