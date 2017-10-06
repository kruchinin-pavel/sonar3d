package org.kpa.util.xml;

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by krucpav on 15.02.17.
 */
public class NLSIter {
    public static final DateTimeFormatter LDF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter LDF2 = DateTimeFormatter.ofPattern("d-MM-yyyy");
    private final LinkedList<Node> nodes;
    private final boolean strict;

    private NLSIter(NLSIter item, boolean strict) {
        this.strict = strict;
        nodes = item.nodes;
    }

    public NLSIter(NodeList list) {
        nodes = new LinkedList<>();
        strict = false;
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                nodes.add(node);
            }
        }
    }

    public BigDecimal nextBD(String nodeName) {
        return nextBD(nodeName, null);
    }

    public BigDecimal nextBD(String nodeName, BigDecimal defaultValue) {
        String str = null;
        try {
            str = nextStr(nodeName, null);
            if (str == null) {
                return defaultValue;
            }
            if (str.contains(",") && !str.contains(".")) {
                str = str.replace(",", ".");
            }
            if (str.contains(" ")) {
                str = str.replace(" ", "");
            }
            if (StringUtils.isEmpty(MoreObjects.firstNonNull(str, "").replace("-", "")) && !strict) {
                return null;
            }
            return new BigDecimal(str);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Error parsing node[" + nodeName + "] as number with value [" + str + "]. Error:" + e.getMessage(), e);
        }
    }

    public LocalDate nextDate(String nodeName) {
        String text = nextStr(nodeName);
        try {
            return parseDate(text, strict);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Error parsing node [" + nodeName + "] as date with value [" + text + "]." +
                    "Tried yyyy-MM-dd as well as dd-MM-yyyy. Error:" + e.getMessage(), e);
        }
    }

    public static LocalDate parseDate(String originalText, boolean strict) {
        String text = originalText;
        if (StringUtils.isEmpty(MoreObjects.firstNonNull(text, "").replace("-", "")) && !strict) {
            return null;
        }
        if (text.contains(".") && !text.contains("-")) {
            text = text.replace(".", "-");
        }
        if (text.contains("/") && !text.contains("-")) {
            text = text.replace("/", "-");
        }
        try {
            return LocalDate.parse(text, LDF);
        } catch (DateTimeParseException e) {
            return LocalDate.parse(text, LDF2);
        }
    }

    private static final DateTimeFormatter LDTF = DateTimeFormatter.ISO_DATE_TIME;//ofPattern("yyyy-MM-dd HH:mm:ss");

    public LocalDateTime nextLdt(String nodeName) {
        return LocalDateTime.parse(nextStr("time"), LDTF);
    }

    public String nextStr(String nodeName) {
        return nextStr(nodeName, null);
    }

    public String nextStr(String nodeName, String defaultValue) {
        Node next = next(nodeName);
        if (next == null) {
            return defaultValue;
        }
        String textContent = next.getTextContent();
        if (!StringUtils.isEmpty(textContent)) textContent = textContent.trim();
        return textContent;
    }

    public BigDecimal nextInt(String nodeName) {
        String str = null;
        try {
            str = nextStr(nodeName);
            if (StringUtils.isEmpty(str) && !strict) {
                return null;
            }
            if (str.contains(" ")) {
                str = str.replace(" ", "");
            }
            return BigDecimal.valueOf(Integer.parseInt(str));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Error parsing node [" + nodeName + "] as integer with value [" + str + "]. Error:" + e.getMessage(), e);
        }
    }

    public NLSIter nextIter(String nodeName) {
        Node next = next(nodeName);
        if (next == null) {
            return null;
        }
        return NLSIter.iter(next);
    }

    public boolean hasNext(String nodeName) {
        for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext(); ) {
            Node node = iterator.next();
            if (node.getNodeName().equals(nodeName)) {
                return true;
            }
        }
        return false;
    }

    public Node next(String nodeName) {
        for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext(); ) {
            Node node = iterator.next();
            if (extractNodeName(node.getNodeName()).equals(nodeName)) {
                iterator.remove();
                return node;
            }
        }
        if (strict) {
            Set<String> nodeNames = restNodeNames();
            throw new IllegalArgumentException("Not found node [" + nodeName + "], but next nodes are available: " + nodeNames);
        }
        return null;
    }

    public Set<String> restNodeNames() {
        Set<String> nodeNames = new HashSet<>();
        nodes.forEach(node -> nodeNames.add(node.getNodeName().toLowerCase()));
        return nodeNames;
    }

    public static String extractNodeName(String nodeNameStr) {
        if (nodeNameStr.contains(":")) {
            nodeNameStr = nodeNameStr.substring(nodeNameStr.indexOf(":") + 1);
        }
        return nodeNameStr;
    }

    public static NLSIter iter(InputStream is) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return NLSIter.iter(db.parse(is)).strict();
    }

    public static NLSIter iter(Node node) {
        return new NLSIter(node.getChildNodes());
    }

    public NLSIter strict() {
        return new NLSIter(this, true);
    }
}
