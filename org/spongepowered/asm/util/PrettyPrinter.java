/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.spongepowered.asm.util;

import com.google.common.base.Strings;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PrettyPrinter {
    private final HorizontalRule horizontalRule = new HorizontalRule('*');
    private final List<Object> lines = new ArrayList<Object>();
    private Table table;
    private boolean recalcWidth = false;
    protected int width = 100;
    protected int wrapWidth = 80;
    protected int kvKeyWidth = 10;
    protected String kvFormat = PrettyPrinter.makeKvFormat(this.kvKeyWidth);

    public PrettyPrinter() {
        this(100);
    }

    public PrettyPrinter(int width) {
        this.width = width;
    }

    public PrettyPrinter wrapTo(int wrapWidth) {
        this.wrapWidth = wrapWidth;
        return this;
    }

    public int wrapTo() {
        return this.wrapWidth;
    }

    public PrettyPrinter table() {
        this.table = new Table();
        return this;
    }

    public PrettyPrinter table(String ... titles) {
        this.table = new Table();
        String[] arrstring = titles;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String title = arrstring[n2];
            this.table.addColumn(title);
            ++n2;
        }
        return this;
    }

    public PrettyPrinter table(Object ... format) {
        this.table = new Table();
        Column column = null;
        Object[] arrobject = format;
        int n = arrobject.length;
        int n2 = 0;
        while (n2 < n) {
            Object entry = arrobject[n2];
            if (entry instanceof String) {
                column = this.table.addColumn((String)entry);
            } else if (entry instanceof Integer && column != null) {
                int width = (Integer)entry;
                if (width > 0) {
                    column.setWidth(width);
                } else if (width < 0) {
                    column.setMaxWidth(-width);
                }
            } else if (entry instanceof Alignment && column != null) {
                column.setAlignment((Alignment)((Object)entry));
            } else if (entry != null) {
                column = this.table.addColumn(entry.toString());
            }
            ++n2;
        }
        return this;
    }

    public PrettyPrinter spacing(int spacing) {
        if (this.table == null) {
            this.table = new Table();
        }
        this.table.setColSpacing(spacing);
        return this;
    }

    public PrettyPrinter th() {
        return this.th(false);
    }

    private PrettyPrinter th(boolean onlyIfNeeded) {
        if (this.table == null) {
            this.table = new Table();
        }
        if (onlyIfNeeded) {
            if (!this.table.addHeader) return this;
        }
        this.table.headerAdded();
        this.addLine(this.table);
        return this;
    }

    public PrettyPrinter tr(Object ... args) {
        this.th(true);
        this.addLine(this.table.addRow(args));
        this.recalcWidth = true;
        return this;
    }

    public PrettyPrinter add() {
        this.addLine("");
        return this;
    }

    public PrettyPrinter add(String string) {
        this.addLine(string);
        this.width = Math.max(this.width, string.length());
        return this;
    }

    public PrettyPrinter add(String format, Object ... args) {
        String line = String.format(format, args);
        this.addLine(line);
        this.width = Math.max(this.width, line.length());
        return this;
    }

    public PrettyPrinter add(Object[] array) {
        return this.add(array, "%s");
    }

    public PrettyPrinter add(Object[] array, String format) {
        Object[] arrobject = array;
        int n = arrobject.length;
        int n2 = 0;
        while (n2 < n) {
            Object element = arrobject[n2];
            this.add(format, element);
            ++n2;
        }
        return this;
    }

    public PrettyPrinter addIndexed(Object[] array) {
        int indexWidth = String.valueOf(array.length - 1).length();
        String format = "[%" + indexWidth + "d] %s";
        int index = 0;
        while (index < array.length) {
            this.add(format, index, array[index]);
            ++index;
        }
        return this;
    }

    public PrettyPrinter addWithIndices(Collection<?> c) {
        return this.addIndexed(c.toArray());
    }

    public PrettyPrinter add(IPrettyPrintable printable) {
        if (printable == null) return this;
        printable.print(this);
        return this;
    }

    public PrettyPrinter add(Throwable th) {
        return this.add(th, 4);
    }

    public PrettyPrinter add(Throwable th, int indent) {
        while (th != null) {
            this.add("%s: %s", th.getClass().getName(), th.getMessage());
            this.add(th.getStackTrace(), indent);
            th = th.getCause();
        }
        return this;
    }

    public PrettyPrinter add(StackTraceElement[] stackTrace, int indent) {
        String margin = Strings.repeat(" ", indent);
        StackTraceElement[] arrstackTraceElement = stackTrace;
        int n = arrstackTraceElement.length;
        int n2 = 0;
        while (n2 < n) {
            StackTraceElement st = arrstackTraceElement[n2];
            this.add("%s%s", margin, st);
            ++n2;
        }
        return this;
    }

    public PrettyPrinter add(Object object) {
        return this.add(object, 0);
    }

    public PrettyPrinter add(Object object, int indent) {
        String margin = Strings.repeat(" ", indent);
        return this.append(object, indent, margin);
    }

    private PrettyPrinter append(Object object, int indent, String margin) {
        if (object instanceof String) {
            return this.add("%s%s", margin, object);
        }
        if (object instanceof Iterable) {
            Iterator iterator = ((Iterable)object).iterator();
            while (iterator.hasNext()) {
                Object entry = iterator.next();
                this.append(entry, indent, margin);
            }
            return this;
        }
        if (object instanceof Map) {
            this.kvWidth(indent);
            return this.add((Map)object);
        }
        if (object instanceof IPrettyPrintable) {
            return this.add((IPrettyPrintable)object);
        }
        if (object instanceof Throwable) {
            return this.add((Throwable)object, indent);
        }
        if (!object.getClass().isArray()) return this.add("%s%s", margin, object);
        return this.add((Object[])object, indent + "%s");
    }

    public PrettyPrinter addWrapped(String format, Object ... args) {
        return this.addWrapped(this.wrapWidth, format, args);
    }

    public PrettyPrinter addWrapped(int width, String format, Object ... args) {
        String indent = "";
        String line = String.format(format, args).replace("\t", "    ");
        Matcher indentMatcher = Pattern.compile("^(\\s+)(.*)$").matcher(line);
        if (indentMatcher.matches()) {
            indent = indentMatcher.group(1);
        }
        try {
            Iterator<String> iterator = this.getWrapped(width, line, indent).iterator();
            while (iterator.hasNext()) {
                String wrappedLine = iterator.next();
                this.addLine(wrappedLine);
            }
            return this;
        }
        catch (Exception ex) {
            this.add(line);
        }
        return this;
    }

    private List<String> getWrapped(int width, String line, String indent) {
        ArrayList<String> lines = new ArrayList<String>();
        do {
            if (line.length() <= width) {
                if (line.length() <= 0) return lines;
                lines.add(line);
                return lines;
            }
            int wrapPoint = line.lastIndexOf(32, width);
            if (wrapPoint < 10) {
                wrapPoint = width;
            }
            String head = line.substring(0, wrapPoint);
            lines.add(head);
            line = indent + line.substring(wrapPoint + 1);
        } while (true);
    }

    public PrettyPrinter kv(String key, String format, Object ... args) {
        return this.kv(key, String.format(format, args));
    }

    public PrettyPrinter kv(String key, Object value) {
        this.addLine(new KeyValue(key, value));
        return this.kvWidth(key.length());
    }

    public PrettyPrinter kvWidth(int width) {
        if (width > this.kvKeyWidth) {
            this.kvKeyWidth = width;
            this.kvFormat = PrettyPrinter.makeKvFormat(width);
        }
        this.recalcWidth = true;
        return this;
    }

    public PrettyPrinter add(Map<?, ?> map) {
        Iterator<Map.Entry<?, ?>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<?, ?> entry = iterator.next();
            String key = entry.getKey() == null ? "null" : entry.getKey().toString();
            this.kv(key, entry.getValue());
        }
        return this;
    }

    public PrettyPrinter hr() {
        return this.hr('*');
    }

    public PrettyPrinter hr(char ruleChar) {
        this.addLine(new HorizontalRule(ruleChar));
        return this;
    }

    public PrettyPrinter centre() {
        if (this.lines.isEmpty()) return this;
        Object lastLine = this.lines.get(this.lines.size() - 1);
        if (!(lastLine instanceof String)) return this;
        this.addLine(new CentredText(this.lines.remove(this.lines.size() - 1)));
        return this;
    }

    private void addLine(Object line) {
        if (line == null) {
            return;
        }
        this.lines.add(line);
        this.recalcWidth |= line instanceof IVariableWidthEntry;
    }

    public PrettyPrinter trace() {
        return this.trace(PrettyPrinter.getDefaultLoggerName());
    }

    public PrettyPrinter trace(Level level) {
        return this.trace(PrettyPrinter.getDefaultLoggerName(), level);
    }

    public PrettyPrinter trace(String logger) {
        return this.trace(System.err, LogManager.getLogger((String)logger));
    }

    public PrettyPrinter trace(String logger, Level level) {
        return this.trace(System.err, LogManager.getLogger((String)logger), level);
    }

    public PrettyPrinter trace(Logger logger) {
        return this.trace(System.err, logger);
    }

    public PrettyPrinter trace(Logger logger, Level level) {
        return this.trace(System.err, logger, level);
    }

    public PrettyPrinter trace(PrintStream stream) {
        return this.trace(stream, PrettyPrinter.getDefaultLoggerName());
    }

    public PrettyPrinter trace(PrintStream stream, Level level) {
        return this.trace(stream, PrettyPrinter.getDefaultLoggerName(), level);
    }

    public PrettyPrinter trace(PrintStream stream, String logger) {
        return this.trace(stream, LogManager.getLogger((String)logger));
    }

    public PrettyPrinter trace(PrintStream stream, String logger, Level level) {
        return this.trace(stream, LogManager.getLogger((String)logger), level);
    }

    public PrettyPrinter trace(PrintStream stream, Logger logger) {
        return this.trace(stream, logger, Level.DEBUG);
    }

    public PrettyPrinter trace(PrintStream stream, Logger logger, Level level) {
        this.log(logger, level);
        this.print(stream);
        return this;
    }

    public PrettyPrinter print() {
        return this.print(System.err);
    }

    public PrettyPrinter print(PrintStream stream) {
        this.updateWidth();
        this.printSpecial(stream, this.horizontalRule);
        Iterator<Object> iterator = this.lines.iterator();
        do {
            if (!iterator.hasNext()) {
                this.printSpecial(stream, this.horizontalRule);
                return this;
            }
            Object line = iterator.next();
            if (line instanceof ISpecialEntry) {
                this.printSpecial(stream, (ISpecialEntry)line);
                continue;
            }
            this.printString(stream, line.toString());
        } while (true);
    }

    private void printSpecial(PrintStream stream, ISpecialEntry line) {
        stream.printf("/*%s*/\n", line.toString());
    }

    private void printString(PrintStream stream, String string) {
        if (string == null) return;
        stream.printf("/* %-" + this.width + "s */\n", string);
    }

    public PrettyPrinter log(Logger logger) {
        return this.log(logger, Level.INFO);
    }

    public PrettyPrinter log(Logger logger, Level level) {
        this.updateWidth();
        this.logSpecial(logger, level, this.horizontalRule);
        Iterator<Object> iterator = this.lines.iterator();
        do {
            if (!iterator.hasNext()) {
                this.logSpecial(logger, level, this.horizontalRule);
                return this;
            }
            Object line = iterator.next();
            if (line instanceof ISpecialEntry) {
                this.logSpecial(logger, level, (ISpecialEntry)line);
                continue;
            }
            this.logString(logger, level, line.toString());
        } while (true);
    }

    private void logSpecial(Logger logger, Level level, ISpecialEntry line) {
        logger.log(level, "/*{}*/", new Object[]{line.toString()});
    }

    private void logString(Logger logger, Level level, String line) {
        if (line == null) return;
        logger.log(level, String.format("/* %-" + this.width + "s */", line));
    }

    private void updateWidth() {
        if (!this.recalcWidth) return;
        this.recalcWidth = false;
        Iterator<Object> iterator = this.lines.iterator();
        while (iterator.hasNext()) {
            Object line = iterator.next();
            if (!(line instanceof IVariableWidthEntry)) continue;
            this.width = Math.min(4096, Math.max(this.width, ((IVariableWidthEntry)line).getWidth()));
        }
    }

    private static String makeKvFormat(int keyWidth) {
        return String.format("%%%ds : %%s", keyWidth);
    }

    private static String getDefaultLoggerName() {
        String string;
        String name = new Throwable().getStackTrace()[2].getClassName();
        int pos = name.lastIndexOf(46);
        if (pos == -1) {
            string = name;
            return string;
        }
        string = name.substring(pos + 1);
        return string;
    }

    public static void dumpStack() {
        new PrettyPrinter().add(new Exception("Stack trace")).print(System.err);
    }

    public static void print(Throwable th) {
        new PrettyPrinter().add(th).print(System.err);
    }

    static class Row
    implements IVariableWidthEntry {
        final Table table;
        final String[] args;

        public Row(Table table, Object ... args) {
            this.table = table.grow(args.length);
            this.args = new String[args.length];
            int i = 0;
            while (i < args.length) {
                this.args[i] = args[i].toString();
                this.table.columns.get(i).setMinWidth(this.args[i].length());
                ++i;
            }
        }

        public String toString() {
            Object[] args = new Object[this.table.columns.size()];
            int col = 0;
            while (col < args.length) {
                Column column = this.table.columns.get(col);
                args[col] = col >= this.args.length ? "" : (this.args[col].length() > column.getMaxWidth() ? this.args[col].substring(0, column.getMaxWidth()) : this.args[col]);
                ++col;
            }
            return String.format(this.table.format, args);
        }

        @Override
        public int getWidth() {
            return this.toString().length();
        }
    }

    static class Column {
        private final Table table;
        private Alignment align = Alignment.LEFT;
        private int minWidth = 1;
        private int maxWidth = Integer.MAX_VALUE;
        private int size = 0;
        private String title = "";
        private String format = "%s";

        Column(Table table) {
            this.table = table;
        }

        Column(Table table, String title) {
            this(table);
            this.title = title;
            this.minWidth = title.length();
            this.updateFormat();
        }

        Column(Table table, Alignment align, int size, String title) {
            this(table, title);
            this.align = align;
            this.size = size;
        }

        void setAlignment(Alignment align) {
            this.align = align;
            this.updateFormat();
        }

        void setWidth(int width) {
            if (width <= this.size) return;
            this.size = width;
            this.updateFormat();
        }

        void setMinWidth(int width) {
            if (width <= this.minWidth) return;
            this.minWidth = width;
            this.updateFormat();
        }

        void setMaxWidth(int width) {
            this.size = Math.min(this.size, this.maxWidth);
            this.maxWidth = Math.max(1, width);
            this.updateFormat();
        }

        void setTitle(String title) {
            this.title = title;
            this.setWidth(title.length());
        }

        private void updateFormat() {
            int width = Math.min(this.maxWidth, this.size == 0 ? this.minWidth : this.size);
            this.format = "%" + (this.align == Alignment.RIGHT ? "" : "-") + width + "s";
            this.table.updateFormat();
        }

        int getMaxWidth() {
            return this.maxWidth;
        }

        String getTitle() {
            return this.title;
        }

        String getFormat() {
            return this.format;
        }

        public String toString() {
            if (this.title.length() <= this.maxWidth) return this.title;
            return this.title.substring(0, this.maxWidth);
        }
    }

    static class Table
    implements IVariableWidthEntry {
        final List<Column> columns = new ArrayList<Column>();
        final List<Row> rows = new ArrayList<Row>();
        String format = "%s";
        int colSpacing = 2;
        boolean addHeader = true;

        Table() {
        }

        void headerAdded() {
            this.addHeader = false;
        }

        void setColSpacing(int spacing) {
            this.colSpacing = Math.max(0, spacing);
            this.updateFormat();
        }

        Table grow(int size) {
            do {
                if (this.columns.size() >= size) {
                    this.updateFormat();
                    return this;
                }
                this.columns.add(new Column(this));
            } while (true);
        }

        Column add(Column column) {
            this.columns.add(column);
            return column;
        }

        Row add(Row row) {
            this.rows.add(row);
            return row;
        }

        Column addColumn(String title) {
            return this.add(new Column(this, title));
        }

        Column addColumn(Alignment align, int size, String title) {
            return this.add(new Column(this, align, size, title));
        }

        Row addRow(Object ... args) {
            return this.add(new Row(this, args));
        }

        void updateFormat() {
            String spacing = Strings.repeat(" ", this.colSpacing);
            StringBuilder format = new StringBuilder();
            boolean addSpacing = false;
            Iterator<Column> iterator = this.columns.iterator();
            do {
                if (!iterator.hasNext()) {
                    this.format = format.toString();
                    return;
                }
                Column column = iterator.next();
                if (addSpacing) {
                    format.append(spacing);
                }
                addSpacing = true;
                format.append(column.getFormat());
            } while (true);
        }

        String getFormat() {
            return this.format;
        }

        Object[] getTitles() {
            ArrayList<String> titles = new ArrayList<String>();
            Iterator<Column> iterator = this.columns.iterator();
            while (iterator.hasNext()) {
                Column column = iterator.next();
                titles.add(column.getTitle());
            }
            return titles.toArray();
        }

        public String toString() {
            boolean nonEmpty = false;
            String[] titles = new String[this.columns.size()];
            for (int col = 0; col < this.columns.size(); nonEmpty |= titles[col].isEmpty() == false, ++col) {
                titles[col] = this.columns.get(col).toString();
            }
            if (!nonEmpty) return null;
            String string = String.format(this.format, titles);
            return string;
        }

        @Override
        public int getWidth() {
            String str = this.toString();
            if (str == null) return 0;
            int n = str.length();
            return n;
        }
    }

    public static enum Alignment {
        LEFT,
        RIGHT;
        
    }

    class CentredText {
        private final Object centred;

        public CentredText(Object centred) {
            this.centred = centred;
        }

        public String toString() {
            String text = this.centred.toString();
            return String.format("%" + ((PrettyPrinter.this.width - text.length()) / 2 + text.length()) + "s", text);
        }
    }

    class HorizontalRule
    implements ISpecialEntry {
        private final char[] hrChars;

        public HorizontalRule(char ... hrChars) {
            this.hrChars = hrChars;
        }

        public String toString() {
            return Strings.repeat(new String(this.hrChars), PrettyPrinter.this.width + 2);
        }
    }

    class KeyValue
    implements IVariableWidthEntry {
        private final String key;
        private final Object value;

        public KeyValue(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public String toString() {
            return String.format(PrettyPrinter.this.kvFormat, this.key, this.value);
        }

        @Override
        public int getWidth() {
            return this.toString().length();
        }
    }

    static interface ISpecialEntry {
    }

    static interface IVariableWidthEntry {
        public int getWidth();
    }

    public static interface IPrettyPrintable {
        public void print(PrettyPrinter var1);
    }

}

