package server;

import org.w3c.dom.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.*;

import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class XMLDom {

    private static final Path XML   = Paths.get("jogadores.xml");
    private static final Path XSD   = Paths.get("jogadores.xsd");

    /* ===================== LEITURA ===================== */
    public static Map<String, RegistoJogador> carregar() {
        if (Files.notExists(XML)) return new ConcurrentHashMap<>();

        try (InputStream in = Files.newInputStream(XML)) {

            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setIgnoringComments(true);
            f.setNamespaceAware(true);

            /* valida com o XSD durante o parsing */
            Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                                         .newSchema(XSD.toFile());
            f.setSchema(schema);

            DocumentBuilder db = f.newDocumentBuilder();
            Document doc = db.parse(in);

            Map<String, RegistoJogador> mapa = new ConcurrentHashMap<>();
            NodeList lista = doc.getElementsByTagName("jogador");

            for (int i = 0; i < lista.getLength(); i++) {
                Element e = (Element) lista.item(i);

                String nick   = e.getAttribute("nickname");
                String pass   = texto(e, "password");
                String nac    = texto(e, "nacionalidade");
                int    idade  = inteiro(e, "idade");
                String caminho = texto(e, "caminhoFoto");
                
                int    vit    = inteiro(e, "vitorias");
                int    der    = inteiro(e, "derrotas");

                RegistoJogador j = new RegistoJogador(nick, pass, nac, idade, caminho);
                
                NodeList tts = e.getElementsByTagName("tempoTotal");
                if (tts.getLength() > 0) {                    // existe?
                    String iso = tts.item(0).getTextContent();// pode estar vazio
                    if (!iso.isEmpty()) {                     // e tem valor?
                        j.adicionarTempo(Duration.parse(iso));
                    }
                }    // repõe o acumulado
                
                
                /* repõe estatísticas */
                for (int v = 0; v < vit; v++) j.registarVitoria();
                for (int d = 0; d < der; d++) j.registarDerrota();

                mapa.put(nick, j);
            }
            return mapa;

        } catch (Exception ex) {
            System.err.println("Erro a ler jogadores.xml – ficheiro será ignorado.");
            ex.printStackTrace();
            return new ConcurrentHashMap<>();
        }
    }

    /* ===================== GRAVAÇÃO ===================== */
    public static synchronized void guardar(Map<String, RegistoJogador> mapa) {

        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            Document doc = f.newDocumentBuilder().newDocument();

            Element raiz = doc.createElement("jogadores");
            doc.appendChild(raiz);

            for (RegistoJogador j : mapa.values()) {
                Element e = doc.createElement("jogador");
                e.setAttribute("nickname", j.getNickname());
                filho(doc, e, "password",      j.getPassword());
                filho(doc, e, "nacionalidade", j.getNacionalidade());
                filho(doc, e, "idade",         String.valueOf(j.getIdade()));
                filho(doc, e, "caminhoFoto",  j.getCaminhoFoto());
          
                filho(doc, e, "vitorias",      String.valueOf(j.getVitorias()));
                filho(doc, e, "derrotas",      String.valueOf(j.getDerrotas()));
                filho(doc, e, "tempoTotal", j.getTempoTotal().toString());

                
                raiz.appendChild(e);
            }
            

            /* pretty print */
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            try (Writer w = Files.newBufferedWriter(XML)) {
                tf.transform(new DOMSource(doc), new StreamResult(w));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /* ======== helpers DOM ======== */
    private static void filho(Document d, Element pai, String nome, String valor) {
        Element n = d.createElement(nome);
        n.appendChild(d.createTextNode(valor));
        pai.appendChild(n);
    }

    private static String texto(Element e, String tag) {
        return e.getElementsByTagName(tag).item(0).getTextContent();
    }

    private static int inteiro(Element e, String tag) {
        return Integer.parseInt(texto(e, tag));
    }

    private XMLDom() {}
    
   
}
