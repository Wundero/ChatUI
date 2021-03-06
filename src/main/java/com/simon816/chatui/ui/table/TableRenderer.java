package com.simon816.chatui.ui.table;

import com.simon816.chatui.PlayerContext;
import org.spongepowered.api.text.Text;

import java.util.List;

public interface TableRenderer {

    interface TableViewport {

        int getFirstRowIndex();

        int getFirstColumnIndex();

    }

    TableViewport getViewport();

    List<Text> renderCellValue(Object value, int row, int column, TableModel model, PlayerContext ctx);

    Text applySideBorders(int rowIndex, List<Text> line, int[] colMaxWidths);

    Text createBorder(TableModel model, int rowIndex, int[] colMaxWidths);

    default int modifyMaxWidth(int index, int max) {
        return max;
    }

}
