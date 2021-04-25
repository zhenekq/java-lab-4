import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
    // Константы с исходным размером окна приложения
    private static final int WIDTH = 700;
    private static final int HEIGHT = 500;
    // Объект диалогового окна для выбора файлов.
    // Компонент не создается изначально, т.к. может и не понадобиться
    // пользователю если тот не собирается сохранять данные в файл
    private JFileChooser fileChooser = null;
    // Элементы меню вынесены в поля данных класса, так как ими необходимо
    // манипулировать из разных мест
    private JMenuItem saveToTextMenuItem;
    private JMenuItem searchValueMenuItem;
    private JMenuItem aboutProgramMenuItem;
    // Поля ввода для считывания значений переменных
    private JTextField textFieldFrom;
    private JTextField textFieldTo;
    private JTextField textFieldStep;
    private JTextField textFieldParameter;
    private Box hBoxResult;
    // Визуализатор ячеек таблицы
    private FunctionTableCellRenderer renderer = new FunctionTableCellRenderer();
    // Модель данных с результатами вычислений

    private FunctionTableModel data;

    public MainFrame() {
        // Обязательный вызов конструктора предка
        super("Табулирование функции на отрезке");
        // Установить размеры окна
        setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        // Отцентрировать окно приложения на экране
        setLocation((kit.getScreenSize().width - WIDTH) / 2, (kit.getScreenSize().height - HEIGHT) / 2);
        // Создать полосу меню и установить ее в верхнюю часть фрейма
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        // Создать и добавить меню верхнего уровня "Файл"
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);
        // Создать и добавить меню верхнего уровня "Таблица"
        JMenu tableMenu = new JMenu("Таблица");
        menuBar.add(tableMenu);
        // Создать и добавить меню верхнего уровня "Справка"
        JMenu helpMenu = new JMenu("Справка");
        menuBar.add(helpMenu);
        // Создать новое "действие" по сохранению в текстовый файл
        Action saveToTextAction = new AbstractAction("Сохранить в текстовый файл") {
            public void actionPerformed(ActionEvent event) {
                // Если экземпляр диалогового окна "Открыть файл" еще не создан,
                // то создать его и инициализировать текущей директорией
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                // Показать диалоговое окно
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION)
                    // Если файл выбран, сохранить данные в текстовый файл
                    saveToTextFile(fileChooser.getSelectedFile());
            }
        };
        // Добавить соответствующий пункт меню в меню "Файл"
        saveToTextMenuItem = fileMenu.add(saveToTextAction);
        // По умолчанию пункт меню является недоступным (данных еще нет)
        saveToTextMenuItem.setEnabled(false);
        // Создать новое действие по поиску значений функции
        Action searchValueAction = new AbstractAction("Найти значение функции") {
            public void actionPerformed(ActionEvent event) {
                // Запросить пользователя ввести искомую строку
                String value = JOptionPane.showInputDialog(MainFrame.this, "Введите значение для поиска",
                        "Поиск значения", JOptionPane.QUESTION_MESSAGE);
                // Установить введенное значение в качестве иголки
                renderer.setNeedle(value);
                // Обновить таблицу
                getContentPane().repaint();
            }

        };
        // Добавить действие в меню "Таблица"
        searchValueMenuItem = tableMenu.add(searchValueAction);
        // По умолчанию пункт меню является недоступным (данных еще нет)
        searchValueMenuItem.setEnabled(false);
        //Создать действие в меню "Справка"
        Action aboutProgramAction = new AbstractAction("О программе") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(getContentPane(), "Возный Евгений 2 курс 4 группа", "Information about program", JOptionPane.PLAIN_MESSAGE);
            }
        };
        //Добавить действие в меню "Справка"
        aboutProgramMenuItem = helpMenu.add(aboutProgramAction);
        //Сделать пункт меню доступным по умолчанию
        aboutProgramMenuItem.setEnabled(true);
        // Создать текстовое поле для ввода значения длиной в 10 символов
        // со значением по умолчанию 0.0
        textFieldFrom = new JTextField("0.0", 10);
        // Установить максимальный размер равный предпочтительному, чтобы
        // предотвратить увеличение размера поля ввода
        textFieldFrom.setMaximumSize(textFieldFrom.getPreferredSize());
        // Создать поле для ввода конечного значения X
        textFieldTo = new JTextField("1.0", 10);
        textFieldTo.setMaximumSize(textFieldTo.getPreferredSize());
        // Создать поле для ввода шага по X
        textFieldStep = new JTextField("0.1", 10);
        textFieldStep.setMaximumSize(textFieldStep.getPreferredSize());
        //Создать поле для ввода параметра
        textFieldParameter = new JTextField("1.0", 10);
        textFieldParameter.setMaximumSize(textFieldParameter.getPreferredSize());
        // Создать контейнер типа "коробка с горизонтальной укладкой"
        Box hboxXRange = Box.createHorizontalBox();
        // Задать для контейнера тип рамки c заголовком
        hboxXRange.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Настройки:"));
        // Добавить "клей"
        hboxXRange.add(Box.createHorizontalGlue());
        // Добавить подпись "X начальное:"
        hboxXRange.add(new JLabel("X начальное:"));
        // Добавить "распорку"
        hboxXRange.add(Box.createHorizontalStrut(10));
        // Добавить поле ввода начального значения X
        hboxXRange.add(textFieldFrom);
        // Добавить "распорку"
        hboxXRange.add(Box.createHorizontalStrut(20));
        // Добавить подпись "X конечное:"
        hboxXRange.add(new JLabel("X конечное:"));
        // Добавить "распорку"
        hboxXRange.add(Box.createHorizontalStrut(10));
        // Добавить поле ввода конечного значения X
        hboxXRange.add(textFieldTo);
        // Добавить "распорку"
        hboxXRange.add(Box.createHorizontalStrut(20));
        // Добавить подпись "Шаг для X:"
        hboxXRange.add(new JLabel("Шаг для X:"));
        // Добавить "распорку"
        hboxXRange.add(Box.createHorizontalStrut(10));
        // Добавить поле для ввода шага для X
        hboxXRange.add(textFieldStep);
        // Добавить "клей"
        hboxXRange.add(Box.createHorizontalGlue());
        // Добавить подпись для параметра
        hboxXRange.add(new JLabel("Множитель для Y:"));
        // Добавить "распорку"
        hboxXRange.add(Box.createHorizontalStrut(10));
        //ДОбавить поле для ввода параметра
        hboxXRange.add(textFieldParameter);
        // Добавить "клей"
        hboxXRange.add(Box.createHorizontalGlue());
        // Установить предпочтительный размер области больше
        // минимального, чтобы при компоновке область совсем не сдавили
        hboxXRange.setPreferredSize(new Dimension(new Double(hboxXRange.getMaximumSize().getWidth()).intValue(),
                new Double(hboxXRange.getMinimumSize().getHeight() * 1.5).intValue()));
        // Установить область в верхнюю (северную) часть компоновки

        getContentPane().add(hboxXRange, BorderLayout.NORTH);
        // Создать кнопку "Вычислить"
        JButton buttonCalc = new JButton("Вычислить");
        // Задать действие на нажатие "Вычислить" и привязать к кнопке
        buttonCalc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
                    // Считать значения начала и конца отрезка, шага
                    Double from = Double.parseDouble(textFieldFrom.getText());
                    Double to = Double.parseDouble(textFieldTo.getText());
                    Double step = Double.parseDouble(textFieldStep.getText());
                    Double parameter = Double.parseDouble(textFieldParameter.getText());
                    // На основе считанных данных создать экземпляр модели таблицы
                    data = new FunctionTableModel(from, to, step, parameter);
                    // Создать новый экземпляр таблицы
                    JTable table = new JTable(data);
                    // Установить в качестве визуализатора ячеек для класса Double
                    // разработанный визуализатор
                    table.setDefaultRenderer(Double.class, renderer);
                    // Установить размер строки таблицы в 30 пикселов
                    table.setRowHeight(30);
                    // Удалить все вложенные элементы из контейнера hBoxResult
                    hBoxResult.removeAll();
                    // Добавить в hBoxResult таблицу, "обернутую" в панель
                    // с полосами прокрутки
                    hBoxResult.add(new JScrollPane(table));
                    // Перерасположить компоненты в hBoxResult и выполнить
                    // перерисовку
                    hBoxResult.revalidate();
                    // Сделать ряд элементов меню доступными
                    saveToTextMenuItem.setEnabled(true);
                    searchValueMenuItem.setEnabled(true);
                } catch (NumberFormatException ex) {
                    // В случае ошибки преобразования чисел показать сообщение об
                    // ошибке
                    JOptionPane.showMessageDialog(MainFrame.this, "Ошибка в формате записи числа с плавающей точкой",
                            "Ошибочный формат числа", JOptionPane.WARNING_MESSAGE);
                    textFieldFrom.setText("0.0");
                    textFieldTo.setText("1.0");
                    textFieldStep.setText("0.1");
                    textFieldParameter.setText("1.0");

                }
            }
        });
        // Создать кнопку "Очистить поля"
        JButton buttonReset = new JButton("Очистить поля");
        // Задать действие на нажатие "Очистить поля" и привязать к кнопке
        buttonReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                // Установить в полях ввода значения по умолчанию
                textFieldFrom.setText("0.0");
                textFieldTo.setText("1.0");
                textFieldStep.setText("0.1");
                textFieldParameter.setText("1.0");
                // Удалить все вложенные элементы контейнера hBoxResult
                hBoxResult.removeAll();
                // Перерисовать сам hBoxResult
                hBoxResult.repaint();
                // Сделать ряд элементов меню недоступными
                saveToTextMenuItem.setEnabled(false);
                searchValueMenuItem.setEnabled(false);
            }
        });

        // Поместить созданные кнопки в контейнер
        Box hboxButtons = Box.createHorizontalBox();
        hboxButtons.setBorder(BorderFactory.createEtchedBorder());
        hboxButtons.add(Box.createHorizontalGlue());
        hboxButtons.add(buttonCalc);
        hboxButtons.add(Box.createHorizontalStrut(30));
        hboxButtons.add(buttonReset);
        hboxButtons.add(Box.createHorizontalGlue());
        // Установить предпочтительный размер области больше минимального, чтобы
        // при компоновке окна область совсем не сдавили
        hboxButtons.setPreferredSize(new Dimension(new Double(hboxButtons.getMaximumSize().getWidth()).intValue(),
                new Double(hboxButtons.getMinimumSize().getHeight() * 1.5).intValue()));
        // Разместить контейнер с кнопками в нижней (южной) области граничной
        // компоновки
        getContentPane().add(hboxButtons, BorderLayout.SOUTH);
        // Область для вывода результата пока что пустая
        hBoxResult = Box.createHorizontalBox();
        // Установить контейнер hBoxResult в главной (центральной) области
        // граничной компоновки
        getContentPane().add(hBoxResult);
    }

    protected void saveToTextFile(File selectedFile) {
        try {
            // Создать новый символьный поток вывода, направленный в указанный файл
            PrintStream out = new PrintStream(selectedFile);
            // Записать в поток вывода заголовочные сведения
            out.println("Результаты табулирования функции:");
            out.println("");
            out.println("Интервал от " + data.getFrom() + " до " + data.getTo() + " с шагом " + data.getStep());
            out.println("====================================================");
            // Записать в поток вывода значения в точках
            for (int i = 0; i < data.getRowCount(); i++)
                out.println("Значение в точке " + data.getValueAt(i, 0) + " равно " + data.getValueAt(i, 1));
            // Закрыть поток
            out.close();
        } catch (FileNotFoundException e) {
            // Исключительную ситуацию "ФайлНеНайден" можно не
            // обрабатывать, так как мы файл создаем, а не открываем
        }
    }

    public static void main(String[] args) {
        // Создать экземпляр главного окна
        MainFrame frame = new MainFrame();
        // Задать действие, выполняемое при закрытии окна
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Показать главное окно приложения
        frame.setVisible(true);
    }
}