import javax.swing.table.AbstractTableModel;
@SuppressWarnings("serial")
public class GornerTableModel extends AbstractTableModel {
    private Double[] coefficients;
    private Double from;
    private Double to;
    private Double step;
    public GornerTableModel(Double from, Double to, Double step,
                            Double[] coefficients) {
        this.from = from;
        this.to = to;
        this.step = step;
        this.coefficients = coefficients;
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
    public int getColumnCount() {
// В данной модели два столбца
        return 4;
    }
    public int getRowCount() {
// Вычислить количество точек между началом и концом отрезка
// исходя из шага табулирования
        return (int)(Math.ceil((to-from)/step))+1;
    }
    public Object getValueAt(int row, int col) {
// Вычислить значение X как НАЧАЛО_ОТРЕЗКА + ШАГ*НОМЕР_СТРОКИ
        double x = from + step * row;
        if (col==0) {
// Если запрашивается значение 1-го столбца, то это X
            return x;
        } else if(col == 1) {
// Если запрашивается значение 2-го столбца, то это значение
// многочлена
            Double result = 0.0;
//
            result = coefficients[0]; // Начинаем с первого коэффициента

            for (int i = 1; i < coefficients.length; i++) {
                result = result * x + coefficients[i]; // Схема Горнера
            }
// ...
            return result;
        }
        else if(col == 2)
        {
            Double result = 0.0;
//
            result = coefficients[coefficients.length-1]; // Начинаем с последнего коэффициента

            for (int i = coefficients.length-2; i >= 0; i--) {
                result = result * x + coefficients[i]; // Схема Горнера
            }
            return result;
        }
        else {
            Double result1 = 0.0;
            result1 = coefficients[0]; // Начинаем с первого коэффициента
            for (int i = 1; i < coefficients.length; i++) {
                result1 = result1 * x + coefficients[i]; // Схема Горнера
            }

            Double result2 = 0.0;
            result2 = coefficients[coefficients.length-1]; // Начинаем с первого коэффициента

            for (int i = coefficients.length-2; i >= 0; i--) {
                result2 = result2 * x + coefficients[i]; // Схема Горнера
            }
            return result1 - result2;
        }
    }
    public String getColumnName(int col) {
        switch (col) {
            case 0:
// Название 1-го столбца
                return "Значение X";
            case 1:
// Название 2-го столбца
                return "Значение многочлена";
// Название 3-го столбца
            case 2:
                return "Обратное значение";
            default:
                return "Разница";
        }
    }
    public Class<?> getColumnClass(int col) {
// И в 1-ом и во 2-ом столбце находятся значения типа Double
        return Double.class;
    }
}