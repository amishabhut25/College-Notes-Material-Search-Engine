import java.util.*;
import javax.swing.*;
import java.awt.*;

public class CollegeNotesSearchEngine {

    // -------------------- Material Model --------------------
    static class Material {
        int id;
        String title, subject, topic, type, semester, keywords, path;

        Material(int id, String title, String subject, String topic,
                 String type, String semester, String keywords, String path) {
            this.id = id;
            this.title = title;
            this.subject = subject;
            this.topic = topic;
            this.type = type;
            this.semester = semester;
            this.keywords = keywords;
            this.path = path;
        }

        public String toString() {
            return "ID: " + id +
                   "\nTitle: " + title +
                   "\nSubject: " + subject +
                   "\nTopic: " + topic +
                   "\nType: " + type +
                   "\nSemester: " + semester +
                   "\nKeywords: " + keywords +
                   "\nResource: " + path + "\n";
        }
    }

    // -------------------- Linked List --------------------
    static class MaterialNode {
        Material data;
        MaterialNode next;
        MaterialNode(Material data) { this.data = data; }
    }

    static MaterialNode head;

    static void addToLinkedList(Material m) {
        MaterialNode newNode = new MaterialNode(m);
        if (head == null) head = newNode;
        else {
            MaterialNode temp = head;
            while (temp.next != null) temp = temp.next;
            temp.next = newNode;
        }
    }

    // -------------------- Hashing --------------------
    static HashMap<Integer, Material> materialTable = new HashMap<>();
    static HashMap<String, ArrayList<Integer>> subjectTable = new HashMap<>();

    static void addToHashing(Material m) {
        materialTable.put(m.id, m);
        String key = m.subject.toLowerCase();
        subjectTable.putIfAbsent(key, new ArrayList<>());
        subjectTable.get(key).add(m.id);
    }

    // -------------------- Trie --------------------
    static class TrieNode {
        HashMap<Character, TrieNode> children = new HashMap<>();
        boolean isEnd;
        HashSet<String> suggestions = new HashSet<>();
    }

    static TrieNode root = new TrieNode();

    static void insertTrie(String word) {
        word = word.toLowerCase().trim();
        if (word.isEmpty()) return;
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current.children.putIfAbsent(ch, new TrieNode());
            current = current.children.get(ch);
            current.suggestions.add(word);
        }
        current.isEnd = true;
    }

    static java.util.List<String> autocomplete(String prefix) {
        prefix = prefix.toLowerCase().trim();
        TrieNode current = root;
        for (char ch : prefix.toCharArray()) {
            if (!current.children.containsKey(ch)) return new ArrayList<>();
            current = current.children.get(ch);
        }
        ArrayList<String> result = new ArrayList<>(current.suggestions);
        Collections.sort(result);
        return result;
    }

    // -------------------- Stack --------------------
    static Stack<String> searchHistory = new Stack<>();

    // -------------------- Sorting --------------------
    static ArrayList<Material> getMaterialList() {
        ArrayList<Material> list = new ArrayList<>();
        MaterialNode temp = head;
        while (temp != null) {
            list.add(temp.data);
            temp = temp.next;
        }
        return list;
    }

    static void sortListByTitle() {
        getMaterialList().sort(Comparator.comparing(m -> m.title.toLowerCase()));
    }

    static void sortListBySubject() {
        getMaterialList().sort(Comparator.comparing(m -> m.subject.toLowerCase()));
    }

    // -------------------- Output Helpers --------------------
    static String allMaterialsText() {
        StringBuilder out = new StringBuilder("========== ALL MATERIALS ==========\n\n");
        MaterialNode temp = head;
        while (temp != null) {
            out.append(temp.data).append("\n");
            temp = temp.next;
        }
        return out.toString();
    }

    static String searchByIdText(int id) {
        Material m = materialTable.get(id);
        return m == null ? "Material not found." : "========== MATERIAL FOUND ==========\n" + m;
    }

    static String searchBySubjectText(String subject) {
        ArrayList<Integer> ids = subjectTable.get(subject.toLowerCase().trim());
        if (ids == null || ids.isEmpty()) return "No materials found for this subject.";
        StringBuilder out = new StringBuilder("========== SUBJECT RESULTS ==========\n\n");
        for (int id : ids) out.append(materialTable.get(id)).append("\n");
        return out.toString();
    }

    static String searchByKeywordText(String keyword) {
        keyword = keyword.toLowerCase().trim();
        java.util.List<String> suggestions = autocomplete(keyword);
        if (suggestions.isEmpty()) return "No matching keyword/prefix found.";

        StringBuilder out = new StringBuilder("========== TRIE SUGGESTIONS ==========\n");
        for (String s : suggestions) out.append("- ").append(s).append("\n");
        out.append("\n========== MATCHING MATERIALS ==========\n\n");

        boolean found = false;
        MaterialNode temp = head;
        while (temp != null) {
            Material m = temp.data;
            String all = (m.title + " " + m.subject + " " + m.topic + " " + m.keywords).toLowerCase();
            if (all.contains(keyword)) {
                out.append(m).append("\n");
                found = true;
            }
            temp = temp.next;
        }
        if (!found) out.append("No material directly matched this keyword.");
        return out.toString();
    }

    static String historyText() {
        if (searchHistory.empty()) return "========== SEARCH HISTORY ==========\n\nNo search history.";
        StringBuilder out = new StringBuilder("========== SEARCH HISTORY ==========\n\n");
        for (int i = searchHistory.size() - 1; i >= 0; i--) out.append("- ").append(searchHistory.get(i)).append("\n");
        return out.toString();
    }

    static String statisticsText() {
        HashMap<String, Integer> count = new HashMap<>();
        MaterialNode temp = head;
        while (temp != null) {
            String subject = temp.data.subject;
            count.put(subject, count.getOrDefault(subject, 0) + 1);
            temp = temp.next;
        }
        StringBuilder out = new StringBuilder("========== STATISTICS ==========\n\n");
        out.append("Total Materials: ").append(materialTable.size()).append("\n\n");
        for (String subject : new TreeSet<>(count.keySet()))
            out.append(subject).append(" : ").append(count.get(subject)).append(" material(s)\n");
        return out.toString();
    }

    static String sortedByTitleText() {
        ArrayList<Material> list = getMaterialList();
        list.sort(Comparator.comparing(m -> m.title.toLowerCase()));
        StringBuilder out = new StringBuilder("========== SORTED BY TITLE ==========\n\n");
        for (Material m : list) out.append(m).append("\n");
        return out.toString();
    }

    static String sortedBySubjectText() {
        ArrayList<Material> list = getMaterialList();
        list.sort(Comparator.comparing(m -> m.subject.toLowerCase()));
        StringBuilder out = new StringBuilder("========== SORTED BY SUBJECT ==========\n\n");
        for (Material m : list) out.append(m).append("\n");
        return out.toString();
    }

    // -------------------- Add/Delete --------------------
    static boolean addMaterial(Material m) {
        if (materialTable.containsKey(m.id)) return false;
        addToLinkedList(m);
        addToHashing(m);
        insertTrie(m.title);
        insertTrie(m.subject);
        insertTrie(m.topic);
        for (String k : m.keywords.split(",")) insertTrie(k);
        return true;
    }

    static boolean deleteMaterial(int id) {
        Material target = materialTable.get(id);
        if (target == null) return false;

        MaterialNode current = head, previous = null;
        while (current != null && current.data.id != id) {
            previous = current;
            current = current.next;
        }
        if (current != null) {
            if (previous == null) head = current.next;
            else previous.next = current.next;
        }
        materialTable.remove(id);
        ArrayList<Integer> ids = subjectTable.get(target.subject.toLowerCase());
        if (ids != null) ids.remove(Integer.valueOf(id));
        return true;
    }

    // -------------------- Sample Data --------------------
    static void loadSampleData() {
        addMaterial(new Material(101, "Stack Notes", "Data Structures", "Stack", "Notes", "3", "stack, data, structures", "DS/StackNotes.pdf"));
        addMaterial(new Material(102, "Linked List Practical", "Data Structures", "Linked List", "Practical", "3", "linked list, practical, node", "DS/LinkedList.java"));
        addMaterial(new Material(103, "SQL Queries", "DBMS", "SQL", "Notes", "3", "sql, database, queries", "DBMS/SQL.pdf"));
        addMaterial(new Material(104, "Previous Year Questions", "Operating System", "Processes", "PYQ", "4", "process, os, pyq", "OS/PYQ.pdf"));
        addMaterial(new Material(105, "Python Basics", "Python", "Variables", "PPT", "2", "python, variables, basics", "Python/Basics.pptx"));
    }

    // -------------------- GUI --------------------
    static JTextArea output;
    static JTextField searchField;
    static JTextField idField;

    static void showOutput(String text) {
        output.setText(text);
        output.setCaretPosition(0);
    }

    static JButton button(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setFocusPainted(false);
        return b;
    }

    static void createGUI() {
        JFrame frame = new JFrame("College Notes & Material Search Engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1050, 700);
        frame.setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(12, 12));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("COLLEGE NOTES & MATERIAL SEARCH ENGINE", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        main.add(title, BorderLayout.NORTH);

        output = new JTextArea();
        output.setFont(new Font("Consolas", Font.PLAIN, 14));
        output.setEditable(false);
        output.setLineWrap(false);
        JScrollPane scroll = new JScrollPane(output);
        main.add(scroll, BorderLayout.CENTER);

        JPanel left = new JPanel(new GridLayout(0, 1, 8, 8));
        left.setPreferredSize(new Dimension(230, 0));

        JButton all = button("1. Display All");
        JButton add = button("2. Add Material");
        JButton byId = button("3. Search by ID");
        JButton bySubject = button("4. Search by Subject");
        JButton trie = button("5. Search / Autocomplete");
        JButton titleSort = button("6. Sort by Title");
        JButton subjectSort = button("7. Sort by Subject");
        JButton delete = button("8. Delete Material");
        JButton history = button("9. Search History");
        JButton stats = button("10. Statistics");

        left.add(all); left.add(add); left.add(byId); left.add(bySubject); left.add(trie);
        left.add(titleSort); left.add(subjectSort); left.add(delete); left.add(history); left.add(stats);
        main.add(left, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 8));
        searchField = new JTextField();
        searchField.setToolTipText("Enter keyword, prefix or subject");
        JButton quickSearch = button("Quick Trie Search");
        searchPanel.add(new JLabel("  Keyword / Prefix: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(quickSearch, BorderLayout.EAST);
        main.add(searchPanel, BorderLayout.SOUTH);

        all.addActionListener(e -> showOutput(allMaterialsText()));
        titleSort.addActionListener(e -> showOutput(sortedByTitleText()));
        subjectSort.addActionListener(e -> showOutput(sortedBySubjectText()));
        history.addActionListener(e -> showOutput(historyText()));
        stats.addActionListener(e -> showOutput(statisticsText()));

        byId.addActionListener(e -> {
            String s = JOptionPane.showInputDialog(frame, "Enter Material ID:", "Search by Hashing", JOptionPane.QUESTION_MESSAGE);
            if (s == null) return;
            try {
                int id = Integer.parseInt(s.trim());
                searchHistory.push("ID: " + id);
                showOutput(searchByIdText(id));
            } catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Enter a valid numeric ID."); }
        });

        bySubject.addActionListener(e -> {
            String s = JOptionPane.showInputDialog(frame, "Enter Subject:", "Search by Hashing", JOptionPane.QUESTION_MESSAGE);
            if (s == null) return;
            searchHistory.push("Subject: " + s);
            showOutput(searchBySubjectText(s));
        });

        trie.addActionListener(e -> quickTrieSearch(frame));
        quickSearch.addActionListener(e -> quickTrieSearch(frame));

        add.addActionListener(e -> addDialog(frame));

        delete.addActionListener(e -> {
            String s = JOptionPane.showInputDialog(frame, "Enter Material ID to delete:", "Delete Material", JOptionPane.WARNING_MESSAGE);
            if (s == null) return;
            try {
                int id = Integer.parseInt(s.trim());
                if (deleteMaterial(id)) showOutput("Material deleted successfully.\n\n" + allMaterialsText());
                else JOptionPane.showMessageDialog(frame, "Material not found.");
            } catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Enter a valid numeric ID."); }
        });

        showOutput("Welcome!\n\nSelect an option from the left menu.\n\nDS used:\n• Linked List\n• Hashing\n• Trie\n• Stack\n• Sorting");
        frame.setContentPane(main);
        frame.setVisible(true);
    }

    static void quickTrieSearch(JFrame frame) {
        String s = searchField.getText().trim();
        if (s.isEmpty()) {
            s = JOptionPane.showInputDialog(frame, "Enter keyword/prefix:", "Trie Search", JOptionPane.QUESTION_MESSAGE);
            if (s == null) return;
        }
        searchHistory.push("Keyword: " + s);
        showOutput(searchByKeywordText(s));
    }

    static void addDialog(JFrame frame) {
        JTextField id = new JTextField();
        JTextField title = new JTextField();
        JTextField subject = new JTextField();
        JTextField topic = new JTextField();
        JTextField type = new JTextField("Notes");
        JTextField semester = new JTextField("3");
        JTextField keywords = new JTextField();
        JTextField path = new JTextField();

        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));
        p.add(new JLabel("Material ID:")); p.add(id);
        p.add(new JLabel("Title:")); p.add(title);
        p.add(new JLabel("Subject:")); p.add(subject);
        p.add(new JLabel("Topic:")); p.add(topic);
        p.add(new JLabel("Type:")); p.add(type);
        p.add(new JLabel("Semester:")); p.add(semester);
        p.add(new JLabel("Keywords (comma separated):")); p.add(keywords);
        p.add(new JLabel("File/Resource Path:")); p.add(path);

        int result = JOptionPane.showConfirmDialog(frame, p, "Add New Material", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            Material m = new Material(Integer.parseInt(id.getText().trim()), title.getText().trim(),
                    subject.getText().trim(), topic.getText().trim(), type.getText().trim(),
                    semester.getText().trim(), keywords.getText().trim(), path.getText().trim());
            if (addMaterial(m)) showOutput("Material added successfully!\n\n" + m);
            else JOptionPane.showMessageDialog(frame, "ID already exists.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Please enter valid details.");
        }
    }

    public static void main(String[] args) {
        loadSampleData();
        SwingUtilities.invokeLater(CollegeNotesSearchEngine::createGUI);
    }
}
