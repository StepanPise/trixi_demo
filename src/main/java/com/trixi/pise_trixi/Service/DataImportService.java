package com.trixi.pise_trixi.Service;

import com.trixi.pise_trixi.Entity.CastObce;
import com.trixi.pise_trixi.Entity.Obec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.trixi.pise_trixi.Repository.ObecRepository;
import com.trixi.pise_trixi.Repository.CastObceRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataImportService {

    private final ObecRepository obecRepository;
    private final CastObceRepository castObceRepository;
    private static final String DATA_URL = "https://www.smartform.cz/download/kopidlno.xml.zip";

    @Transactional
    public void importData() {
        log.info("Zahajuji stahovani dat: {}", DATA_URL);

        try (InputStream in = URI.create(DATA_URL).toURL().openStream();
             ZipInputStream zipIn = new ZipInputStream(in)) {

            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                if (entry.getName().endsWith(".xml")) {
                    log.info("Nalezen XML soubor: {}, spoustim StAX parser...", entry.getName());
                    parseAndSaveXml(zipIn);
                    break;
                }
                entry = zipIn.getNextEntry();
            }
        } catch (Exception e) {
            log.error("Chyba pri zpracovani dat", e);
            throw new RuntimeException("Import selhal: " + e.getMessage(), e);
        }
    }

    private void parseAndSaveXml(InputStream xmlStream) throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);

        XMLStreamReader reader = factory.createXMLStreamReader(xmlStream);

        Obec obec = null;
        List<CastObce> castiObceList = new ArrayList<>();

        boolean parsingObec = false;
        boolean parsingCast = false;
        StringBuilder textBuffer = new StringBuilder();

        Long kod = null;
        String nazev = null;

        while (reader.hasNext()) {
            int event = reader.next();

            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    String startName = reader.getLocalName();
                    textBuffer.setLength(0);

                    if ("Obec".equals(startName) && !parsingCast) {
                        parsingObec = true;
                        kod = null;
                        nazev = null;
                    } else if ("CastObce".equals(startName)) {
                        parsingCast = true;
                        kod = null;
                        nazev = null;
                    }
                    break;

                case XMLStreamConstants.CHARACTERS:
                    textBuffer.append(reader.getText());
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    String endName = reader.getLocalName();
                    String content = textBuffer.toString().trim();

                    if (parsingObec && !parsingCast) {
                        if ("Kod".equalsIgnoreCase(endName) && kod == null && !content.isEmpty()) {
                            kod = Long.parseLong(content);

                        } else if ("Nazev".equalsIgnoreCase(endName) && nazev == null && !content.isEmpty()) {
                            nazev = content;

                        } else if ("Obec".equals(endName)) {
                            if (kod != null && nazev != null) {
                                obec = new Obec();
                                obec.setKod(kod);
                                obec.setNazev(nazev);
                                obec = obecRepository.save(obec);
                                log.info("Ulozena obec: {} (Kod: {})", obec.getNazev(), obec.getKod());
                            }
                            parsingObec = false;
                        }
                    } else if (parsingCast) {

                        if ("Kod".equalsIgnoreCase(endName) && kod == null && !content.isEmpty()) {
                            kod = Long.parseLong(content);

                        } else if ("Nazev".equalsIgnoreCase(endName) && nazev == null && !content.isEmpty()) {
                            nazev = content;

                        } else if ("CastObce".equals(endName)) {
                            if (kod != null && nazev != null && obec != null) {
                                CastObce cast = new CastObce();
                                cast.setKod(kod);
                                cast.setNazev(nazev);
                                cast.setObec(obec);
                                castiObceList.add(cast);
                                log.info("Nactena cast obce: {} (kod: {})", nazev, kod);
                            }
                            parsingCast = false;

                        }
                    }
                    textBuffer.setLength(0);
                    break;
            }
        }

        if (!castiObceList.isEmpty()) {
            castObceRepository.saveAll(castiObceList);
            log.info("Ulozeno {} casti obce do db.", castiObceList.size());
        } else {
            log.warn("Nebyly nalezeny zadne casti obce");
        }
    }
}
