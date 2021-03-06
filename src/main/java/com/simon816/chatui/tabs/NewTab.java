package com.simon816.chatui.tabs;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.Lists;
import com.simon816.chatui.PlayerChatView;
import com.simon816.chatui.PlayerContext;
import com.simon816.chatui.ChatUI;
import com.simon816.chatui.util.TextUtils;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class NewTab extends Tab {

    private static final Text TITLE = Text.of("New Tab");

    private final List<Button> buttons = Lists.newArrayList();

    public void addButton(Button button) {
        this.buttons.add(button);
    }

    @Override
    public Text getTitle() {
        return TITLE;
    }

    @Override
    public Text draw(PlayerContext ctx) {
        Text.Builder builder = Text.builder();
        checkArgument(ctx.height >= 3, "Height must be at least 3");
        int maxButtonRows = ctx.height / 3;
        int columns = 1;

        while (maxButtonRows < Math.ceil(this.buttons.size() / (float) columns)) {
            columns++;
        }

        int remainingHeight = ctx.height;
        int width = ctx.width / columns;
        while (width % 9 != 0) {
            width -= 1;
        }
        for (int i = 0; i < this.buttons.size() && remainingHeight > 0; i += columns) {
            Text.Builder line1 = Text.builder();
            Text.Builder line2 = Text.builder();
            Text.Builder line3 = Text.builder();
            for (int j = 0; j < columns; j++) {
                if (this.buttons.size() <= i + j) {
                    break;
                }
                Button button = this.buttons.get(i + j);
                Text[] text = drawButton(button, width);
                line1.append(text[0]);
                line2.append(text[1]);
                line3.append(text[2]);
            }
            builder.append(line1.build(), Text.NEW_LINE, line2.build(), Text.NEW_LINE, line3.build(), Text.NEW_LINE);
            remainingHeight -= 3;
        }
        for (int i = 0; i < remainingHeight; i++) {
            builder.append(Text.NEW_LINE);
        }
        return builder.build();
    }

    private Text[] drawButton(Button button, int width) {
        int barWidth = TextUtils.getWidth('│', false) * 2;
        int bwidth = button.getWidth();
        Text buttonText = button.getText();
        if (bwidth > width - barWidth - 3) {
            // Trim down
            String t = buttonText.toPlain();
            while (bwidth > width - barWidth - 3 && t.length() > 0) {
                t = t.substring(0, t.length() - 1);
                bwidth = TextUtils.getStringWidth(t, false) + 6; // 6 is width
                                                                 // of '...'
            }
            buttonText = ((LiteralText.Builder) buttonText.toBuilder()).content(t + "...").build();
        }
        StringBuilder spaces = new StringBuilder();
        spaces.append('│');
        // Not sure why -3 is needed but it works
        TextUtils.padSpaces(spaces, width - bwidth - barWidth - 3);
        spaces.append('│');
        String left = spaces.substring(0, spaces.length() / 2);
        String right = spaces.substring(left.length());
        return new Text[] {
                TextUtils.startRepeatTerminate('┌', '─', '┐', width),
                Text.builder().append(Text.of(left), buttonText, Text.of(right)).build(),
                TextUtils.startRepeatTerminate('└', '─', '┘', width)};
    }

    public static abstract class Button {

        private final String label;
        private int textWidth = -1;

        public Button(String label) {
            this.label = label;
        }

        public Text getText() {
            return Text.builder(this.label).onClick(TextActions.executeCallback(src -> {
                PlayerChatView view = ChatUI.getView(src);
                if (!(view.getWindow().getActiveTab() instanceof NewTab)) {
                    return; // Expired link
                }
                onClick(view);
            })).build();
        }

        protected int getWidth() {
            if (this.textWidth == -1) {
                this.textWidth = TextUtils.getWidth(getText());
            }
            return this.textWidth;
        }

        protected final void replaceWith(Tab replacement, PlayerChatView view) {
            int oldIndex = view.getWindow().getActiveIndex();
            view.getWindow().addTab(replacement, true);
            view.getWindow().removeTab(oldIndex);
            view.update();
        }

        protected abstract void onClick(PlayerChatView view);
    }

    public static class LaunchTabButton extends Button {

        private final Function<PlayerChatView, Tab> tabOpenFunc;

        public LaunchTabButton(String text, Supplier<Tab> tabOpenFunc) {
            this(text, view -> tabOpenFunc.get());
        }

        public LaunchTabButton(String text, Function<PlayerChatView, Tab> tabOpenFunc) {
            super(text);
            this.tabOpenFunc = tabOpenFunc;
        }

        @Override
        protected void onClick(PlayerChatView view) {
            replaceWith(this.tabOpenFunc.apply(view), view);
        }
    }

}
