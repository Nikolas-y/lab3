import java.awt.*; // Импортирует классы для работы с графическим интерфейсом (GUI), такие как компоненты, цвета, размеры и т.д.
import java.awt.event.ActionEvent; // Импортирует класс для действий, таких как нажатия кнопок.
import java.awt.event.ActionListener; // Импортирует интерфейс для обработки событий действий.
import java.io.*; // Импортирует классы для работы с вводом/выводом, такими как чтение и запись файлов.
import javax.imageio.ImageIO; // Импортирует классы для чтения и записи изображений.
import javax.swing.*; // Импортирует классы для создания графического интерфейса, такие как окна, кнопки, меню и т.д.

@SuppressWarnings("serial") // Подавляет предупреждения компилятора о неиспользуемом поле `serialVersionUID`.
public class MainFrame extends JFrame { // Создает основной класс `MainFrame`, который наследуется от `JFrame`.
    // Константы с исходным размером окна приложения
    private static final int WIDTH = 700; // Ширина окна.
    private static final int HEIGHT = 500; // Высота окна.
    // Массив коэффициентов многочлена
    private Double[] coefficients; // Массив для хранения коэффициентов многочлена.
    // Объект диалогового окна для выбора файлов
    // Компонент не создаѐтся изначально, т.к. может и не понадобиться пользователю, если он не собирается сохранять данные в файл
    private JFileChooser fileChooser = null; // Диалог для выбора файлов.
    // Элементы меню вынесены в поля данных класса, так как ими необходимо манипулировать из разных мест
    private JMenuItem saveToTextMenuItem; // Пункт меню для сохранения в текстовый файл.
    private JMenuItem saveToGraphicsMenuItem; // Пункт меню для сохранения данных для графика.
    private JMenuItem searchValueMenuItem; // Пункт меню для поиска значения многочлена.
    private JMenuItem saveToCSVMenuItem; // Пункт меню для сохранения в CSV файл.
    private JMenuItem showInformation; // Пункт меню для отображения информации о программе.
    private JMenuItem selectRangeMenuItem; // Пункт меню для выделения диапазона.
    // Поля ввода для считывания значений переменных
    private JTextField textFieldFrom; // Поле ввода начального значения диапазона.
    private JTextField textFieldTo; // Поле ввода конечного значения диапазона.
    private JTextField textFieldStep; // Поле ввода шага изменения.
    private Box hBoxResult; // Контейнер для отображения результата.
    // Визуализатор ячеек таблицы
    private GornerTableCellRenderer renderer = new GornerTableCellRenderer(); // Класс для визуализации таблицы.
    // Модель данных с результатами вычислений
    private GornerTableModel data; // Модель данных для хранения результатов.

    public MainFrame(Double[] coefficients) { // Конструктор класса с массивом коэффициентов.
        // Обязательный вызов конструктора предка
        super("Табулирование многочлена на отрезке по схеме Горнера"); // Устанавливает заголовок окна.
        // Запоминаем переданные коэффициенты
        this.coefficients = coefficients;
        // Установить размеры окна
        setSize(WIDTH, HEIGHT); // Устанавливает размер окна.
        Toolkit kit = Toolkit.getDefaultToolkit(); // Получает доступ к системному набору инструментов.
        // Отцентрировать окно приложения на экране
        setLocation((kit.getScreenSize().width - WIDTH) / 2, // Вычисляет координаты для центрирования окна.
                (kit.getScreenSize().height - HEIGHT) / 2);
        // Создать меню
        JMenuBar menuBar = new JMenuBar(); // Создает строку главного меню.
        // Установить меню в качестве главного меню приложения
        setJMenuBar(menuBar); // Устанавливает меню в окно.
        // Добавить в меню пункт меню "Файл"
        JMenu fileMenu = new JMenu("Файл"); // Создает пункт меню "Файл".
        // Добавить его в главное меню
        menuBar.add(fileMenu); // Добавляет "Файл" в меню.
        // Создать пункт меню "Таблица"
        JMenu tableMenu = new JMenu("Таблица"); // Создает пункт меню "Таблица".
        // Добавить его в главное меню
        menuBar.add(tableMenu); // Добавляет "Таблица" в меню.

        JMenu infoMenu = new JMenu("Справка"); // Создает пункт меню "Справка".

        menuBar.add(infoMenu); // Добавляет "Справка" в меню.

        // Создать действие для отображения информации о программе
        Action showInfoAction = new AbstractAction("О программе") {
            @Override
            public void actionPerformed(ActionEvent e) { // Обработчик события нажатия.
                ImageIcon imagef1; // Значок для отображения.
                Image image = null;
                try {
                    // Загрузка изображения из файла
                    image = ImageIO.read(new File("src\\images.jpg"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex); // Генерирует исключение, если изображение не найдено.
                }
                // Изменение размера изображения
                Image newimg = image.getScaledInstance(120, 140, java.awt.Image.SCALE_SMOOTH);
                imagef1 = new ImageIcon(newimg); // Создает объект Icon из изображения.

                // Отображение диалогового окна с информацией.
                JOptionPane.showMessageDialog(MainFrame.this, "разработал:\nЖелябин Николай 8 группа",
                        "О программе ", JOptionPane.QUESTION_MESSAGE, imagef1);
            }
        };
        showInformation = infoMenu.add(showInfoAction); // Добавляет пункт "О программе" в меню "Справка".

        // Создать новое "действие" по сохранению в текстовый файл
        Action saveToTextAction = new AbstractAction("Сохранить в текстовый файл") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    // Если диалог выбора файлов еще не создан, создаем его
                    fileChooser = new JFileChooser();
                    // Устанавливаем текущую директорию
                    fileChooser.setCurrentDirectory(new File("."));
                }
                // Показать диалоговое окно
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    // Если файл выбран, сохраняем данные в текстовый файл
                    saveToTextFile(fileChooser.getSelectedFile());
                }
            }
        };
        // Добавить соответствующий пункт подменю в меню "Файл"
        saveToTextMenuItem = fileMenu.add(saveToTextAction); // Добавляет пункт меню для сохранения в текст.
        // По умолчанию пункт меню недоступен (данных пока нет)
        saveToTextMenuItem.setEnabled(false);

        // Создать действие для сохранения в CSV файл
        Action saveToCSVAction = new AbstractAction("Сохранить в CSV файл") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    // Если диалог выбора файлов еще не создан, создаем его
                    fileChooser = new JFileChooser();
                    // Устанавливаем текущую директорию
                    fileChooser.setCurrentDirectory(new File("."));
                }
                // Показать диалоговое окно
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    // Если файл выбран, сохраняем данные в CSV файл
                    saveToCSVFile(fileChooser.getSelectedFile());
                }
            }
        };
        // Добавить соответствующий пункт подменю в меню "Файл"
        saveToCSVMenuItem = fileMenu.add(saveToCSVAction); // Добавляет пункт меню для сохранения в CSV.
        // По умолчанию пункт меню недоступен
        saveToCSVMenuItem.setEnabled(false);

        // Создать действие для сохранения данных для построения графика
        Action saveToGraphicsAction = new AbstractAction("Сохранить данные для построения графика") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    // Если диалог выбора файлов еще не создан, создаем его
                    fileChooser = new JFileChooser();
                    // Устанавливаем текущую директорию
                    fileChooser.setCurrentDirectory(new File("."));
                }
                // Показать диалоговое окно
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    // Если файл выбран, сохраняем данные в графический файл
                    saveToGraphicsFile(fileChooser.getSelectedFile());
                }
            }
        };
        // Добавить соответствующий пункт подменю в меню "Файл"
        saveToGraphicsMenuItem = fileMenu.add(saveToGraphicsAction); // Добавляет пункт меню для сохранения графика.
        // По умолчанию пункт меню недоступен
        saveToGraphicsMenuItem.setEnabled(false);

// Создать новое действие по поиску значений многочлена
        Action searchValueAction = new AbstractAction("Найти значение многочлена") {
            public void actionPerformed(ActionEvent event) {
                // Запросить пользователя ввести значение для поиска
                String value =
                        JOptionPane.showInputDialog(MainFrame.this, "Введите значение для поиска",
                                "Поиск значения", JOptionPane.QUESTION_MESSAGE);
                // Установить введенное значение в качестве "иголки" для поиска
                renderer.setNeedle(value);
                // Обновить таблицу, чтобы отобразить результат поиска
                getContentPane().repaint();
            }
        };
        // Добавить действие в меню "Таблица"
        searchValueMenuItem = tableMenu.add(searchValueAction); // Добавляет пункт меню для поиска значения.
        // По умолчанию пункт меню недоступен (данных еще нет)
        searchValueMenuItem.setEnabled(false);

        // Создать действие для выделения диапазона в таблице
        Action selectRangeMenuAction = new AbstractAction("Выделить диапазон") {
            public void actionPerformed(ActionEvent event) {
                // Запросить пользователя ввести индексы ячеек для выделения диапазона
                String value =
                        JOptionPane.showInputDialog(MainFrame.this, "Введите индексы ячеек для выделения области",
                                "Выделение области", JOptionPane.QUESTION_MESSAGE);

                // Установить диапазон для выделения
                renderer.setRange(value);
                // Обновить таблицу, чтобы отобразить выделенный диапазон
                getContentPane().repaint();
            }
        };
        // Добавить действие в меню "Таблица"
        selectRangeMenuItem = tableMenu.add(selectRangeMenuAction); // Добавляет пункт меню для выделения диапазона.
        // По умолчанию пункт меню недоступен (данных еще нет)
        selectRangeMenuItem.setEnabled(false);

        // Создать область с полями ввода для границ отрезка и шага
        // Создать подпись для ввода левой границы отрезка
        JLabel labelForFrom = new JLabel("X изменяется на интервале от:"); // Метка для поля ввода начальной границы.
        // Создать текстовое поле для ввода значения длиной в 10 символов со значением по умолчанию 0.0
        textFieldFrom = new JTextField("0.0", 10); // Поле ввода начального значения диапазона.
        // Установить максимальный размер равный предпочтительному, чтобы предотвратить увеличение размера поля ввода
        textFieldFrom.setMaximumSize(textFieldFrom.getPreferredSize());
        // Создать подпись для ввода правой границы отрезка
        JLabel labelForTo = new JLabel("до:"); // Метка для поля ввода конечной границы.
        // Создать текстовое поле для ввода значения длиной в 10 символов со значением по умолчанию 1.0
        textFieldTo = new JTextField("1.0", 10); // Поле ввода конечного значения диапазона.
        // Установить максимальный размер равный предпочтительному, чтобы предотвратить увеличение размера поля ввода
        textFieldTo.setMaximumSize(textFieldTo.getPreferredSize());
        // Создать подпись для ввода шага табулирования
        JLabel labelForStep = new JLabel("с шагом:"); // Метка для поля ввода шага изменения.
        // Создать текстовое поле для ввода значения длиной в 10 символов со значением по умолчанию 0.1
        textFieldStep = new JTextField("0.1", 10); // Поле ввода шага табулирования.
        // Установить максимальный размер равный предпочтительному, чтобы предотвратить увеличение размера поля ввода
        textFieldStep.setMaximumSize(textFieldStep.getPreferredSize());

        // Создать контейнер типа "коробка с горизонтальной укладкой"
        Box hboxRange = Box.createHorizontalBox(); // Горизонтальный контейнер для полей ввода и меток.
        // Задать для контейнера тип рамки "объемная"
        hboxRange.setBorder(BorderFactory.createBevelBorder(1)); // Устанавливает рамку вокруг контейнера.
        // Добавить "клей" для выравнивания компонентов
        hboxRange.add(Box.createHorizontalGlue());
        // Добавить метку "От"
        hboxRange.add(labelForFrom);
        // Добавить "распорку" между компонентами
        hboxRange.add(Box.createHorizontalStrut(10));
        // Добавить поле ввода "От"
        hboxRange.add(textFieldFrom);
        // Добавить "распорку"
        hboxRange.add(Box.createHorizontalStrut(20));
        // Добавить метку "До"
        hboxRange.add(labelForTo);
        // Добавить "распорку"
        hboxRange.add(Box.createHorizontalStrut(10));
        // Добавить поле ввода "До"
        hboxRange.add(textFieldTo);
        // Добавить "распорку"
        hboxRange.add(Box.createHorizontalStrut(20));
        // Добавить метку "с шагом"
        hboxRange.add(labelForStep);
        // Добавить "распорку"
        hboxRange.add(Box.createHorizontalStrut(10));
        // Добавить поле для ввода шага табулирования
        hboxRange.add(textFieldStep);
        // Добавить "клей" в конце контейнера
        hboxRange.add(Box.createHorizontalGlue());
        // Установить предпочтительный размер области равным удвоенному минимальному, чтобы при компоновке область совсем не сдавили
        hboxRange.setPreferredSize(new Dimension(
                (int) (hboxRange.getMaximumSize().getWidth()),
                (int) (hboxRange.getMinimumSize().getHeight()) * 2));
        // Установить область в верхнюю (северную) часть компоновки
        getContentPane().add(hboxRange, BorderLayout.NORTH); // Размещает область ввода в верхней части окна.

        // Создать кнопку "Вычислить"
        JButton buttonCalc = new JButton("Вычислить"); // Кнопка для выполнения вычислений.
        // Задать действие на нажатие "Вычислить" и привязать к кнопке
        buttonCalc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
                    // Считать значения начала и конца отрезка, шага
                    Double from =
                            Double.parseDouble(textFieldFrom.getText()); // Преобразование значения из поля "От" в число.
                    Double to =
                            Double.parseDouble(textFieldTo.getText()); // Преобразование значения из поля "До" в число.
                    Double step =
                            Double.parseDouble(textFieldStep.getText()); // Преобразование значения шага в число.

                    // Создать новую модель данных для таблицы
                    data = new GornerTableModel(from, to, step, MainFrame.this.coefficients);
                    // Создать новый экземпляр таблицы
                    JTable table = new JTable(data);
                    // Установить в качестве визуализатора ячеек для класса Double разработанный визуализатор
                    table.setDefaultRenderer(Double.class, renderer);
                    // Установить размер строки таблицы в 30 пикселов
                    table.setRowHeight(30);
                    // Удалить все вложенные элементы из контейнера hBoxResult
                    hBoxResult.removeAll();
                    // Добавить в hBoxResult таблицу, "обернутую" в панель с полосами прокрутки
                    hBoxResult.add(new JScrollPane(table));
                    // Обновить область содержания главного окна
                    getContentPane().validate();
                    // Пометить ряд элементов меню как доступных
                    saveToTextMenuItem.setEnabled(true);
                    saveToGraphicsMenuItem.setEnabled(true);
                    searchValueMenuItem.setEnabled(true);
                    selectRangeMenuItem.setEnabled(true);
                    saveToCSVMenuItem.setEnabled(true);
                } catch (NumberFormatException ex) {
                    // В случае ошибки преобразования чисел показать сообщение об ошибке
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Ошибка в формате записи числа с плавающей точкой", "Ошибочный формат числа",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Создать кнопку "Очистить поля"
        JButton buttonReset = new JButton("Очистить поля"); // Кнопка для очистки полей ввода и результата.
        // Задать действие на нажатие "Очистить поля" и привязать к кнопке
        buttonReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                // Установить в полях ввода значения по умолчанию
                textFieldFrom.setText("0.0");
                textFieldTo.setText("1.0");
                textFieldStep.setText("0.1");
                // Удалить все вложенные элементы контейнера hBoxResult
                hBoxResult.removeAll();
                // Добавить в контейнер пустую панель
                hBoxResult.add(new JPanel());
                // Пометить элементы меню как недоступные
                saveToTextMenuItem.setEnabled(false);
                saveToGraphicsMenuItem.setEnabled(false);
                searchValueMenuItem.setEnabled(false);
                renderer.rangeClear();
                // Обновить область содержания главного окна
                getContentPane().validate();
            }
        });

        // Поместить созданные кнопки в контейнер
        Box hboxButtons = Box.createHorizontalBox(); // Горизонтальный контейнер для кнопок.
        hboxButtons.setBorder(BorderFactory.createBevelBorder(1)); // Устанавливает рамку вокруг контейнера.
        hboxButtons.add(Box.createHorizontalGlue()); // Добавляет "клей" для выравнивания кнопок.
        hboxButtons.add(buttonCalc); // Добавляет кнопку "Вычислить".
        hboxButtons.add(Box.createHorizontalStrut(30)); // Добавляет промежуток между кнопками.
        hboxButtons.add(buttonReset); // Добавляет кнопку "Очистить поля".
        hboxButtons.add(Box.createHorizontalGlue()); // Добавляет "клей" для выравнивания кнопок.
        // Установить предпочтительный размер области равным удвоенному минимальному, чтобы при компоновке окна область не сжималась
        hboxButtons.setPreferredSize(new Dimension(
                (int) (hboxButtons.getMaximumSize().getWidth()),
                (int) (hboxButtons.getMinimumSize().getHeight()) * 2));
        // Разместить контейнер с кнопками в нижней (южной) области граничной компоновки
        getContentPane().add(hboxButtons, BorderLayout.SOUTH);

        // Область для вывода результата пока что пустая
        hBoxResult = Box.createHorizontalBox(); // Горизонтальный контейнер для отображения результата.
        hBoxResult.add(new JPanel()); // Добавляет пустую панель в контейнер.
        // Установить контейнер hBoxResult в главной (центральной) области граничной компоновки
        getContentPane().add(hBoxResult, BorderLayout.CENTER);
    }// Метод для сохранения данных в текстовый файл
    protected void saveToTextFile(File selectedFile) {
        try {
            PrintWriter out = new PrintWriter(selectedFile); // Создаем объект для записи в файл.
            // Проходим по всем строкам данных
            for (int i = 0; i < data.getRowCount(); i++) {
                // Записываем значения X и Y в файл, разделяя их пробелом
                out.println(data.getValueAt(i, 0) + " " + data.getValueAt(i, 1));
            }
            out.close(); // Закрываем файл, чтобы сохранить изменения.
        } catch (FileNotFoundException e) {
            // Если файл не удалось открыть, выводим сообщение об ошибке
            JOptionPane.showMessageDialog(MainFrame.this,
                    "Файл не может быть создан", "Ошибка записи",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // Метод для сохранения данных в CSV файл
    protected void saveToCSVFile(File selectedFile) {
        try {
            PrintWriter out = new PrintWriter(selectedFile); // Создаем объект для записи в файл.
            // Проходим по всем строкам данных
            for (int i = 0; i < data.getRowCount(); i++) {
                // Записываем значения X и Y, разделяя их запятой
                out.println(data.getValueAt(i, 0) + "," + data.getValueAt(i, 1));
            }
            out.close(); // Закрываем файл, чтобы сохранить изменения.
        } catch (FileNotFoundException e) {
            // Если файл не удалось открыть, выводим сообщение об ошибке
            JOptionPane.showMessageDialog(MainFrame.this,
                    "Файл не может быть создан", "Ошибка записи",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // Метод для сохранения данных в файл для построения графика
    protected void saveToGraphicsFile(File selectedFile) {
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(selectedFile));
            // Проходим по всем строкам данных
            for (int i = 0; i < data.getRowCount(); i++) {
                // Записываем значения X и Y в бинарном формате
                out.writeDouble((Double) data.getValueAt(i, 0)); // Записываем значение X.
                out.writeDouble((Double) data.getValueAt(i, 1)); // Записываем значение Y.
            }
            out.close(); // Закрываем файл, чтобы сохранить изменения.
        } catch (Exception e) {
            // Если файл не удалось открыть, выводим сообщение об ошибке
            JOptionPane.showMessageDialog(MainFrame.this,
                    "Файл не может быть создан", "Ошибка записи",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // Основной метод программы (точка входа)
    public static void main(String[] args) {
        // Если аргументы командной строки отсутствуют или их меньше двух
        if (args.length < 2) {
            System.out.println("Невозможно табулировать многочлен, который отсутствует. " +
                    "Введите коэффициенты многочлена в командной строке."); // Выводим сообщение об ошибке.
            System.exit(-1); // Завершаем выполнение программы с кодом ошибки.
        }
        // Создаем массив коэффициентов
        Double[] coefficients = new Double[args.length];
        try {
            // Преобразуем аргументы командной строки в массив чисел
            for (int i = 0; i < args.length; i++) {
                coefficients[i] = Double.parseDouble(args[i]);
            }
        } catch (NumberFormatException ex) {
            // Если введенные данные не являются числами, выводим сообщение об ошибке
            System.out.println("Ошибка преобразования строки '" + args[0] + "' в число типа Double.");
            System.exit(-2); // Завершаем выполнение программы с кодом ошибки.
        }
        // Создаем объект основного окна приложения
        MainFrame frame = new MainFrame(coefficients);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Устанавливаем поведение при закрытии окна (завершение программы).
        frame.setVisible(true); // Отображаем окно приложения.
    }
}