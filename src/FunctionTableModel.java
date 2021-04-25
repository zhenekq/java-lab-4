import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class FunctionTableModel extends AbstractTableModel {
    private Double from, to, step, parametr;

    public FunctionTableModel(Double from, Double to, Double step, Double parametr) {
        this.from = from;
        this.to = to;
        this.step = step;
        this.parametr = parametr;
    }

    public Double getFrom() {
        return from;
    }

    public Double getTo() {
        return to;
    }

    public Double getStep() {
        return step;
    }

    public Double getParameter() {
        return parametr;
    }

    public int getColumnCount() {
        return 3;
    }

    public int getRowCount() {
        // Вычислить количество значений аргумента исходя из шага
        return new Double(Math.ceil((to - from) / step)).intValue() + 1;
    }

    public Object getValueAt(int row, int col) {
        // Вычислить значение X (col=0) как НАЧАЛО_ОТРЕЗКА + ШАГ*НОМЕР_СТРОКИ
        double x = from + step * row;
        double y = parametr - x;
        switch (col){
            case(0):
                return x;
            case(1):
                return y;
            case(2):
                return (int)y != 0;
        }
        // Значение y (col=1) равно х
        return x;
    }

    public String getColumnName(int col) {
        switch (col) {
            case 0: return "Значение X";
            case 1: return "Значение Y";
            case 2:	return "Целая часть Y не равна 0?";

        }
        return "";
    }

    public Class<?> getColumnClass(int col) {
        // И в 1-ом и во 2-ом столбце находятся значения типа Double а в 3-ем Boolean
        return col == 2 ? Boolean.class : Double.class;
    }
}