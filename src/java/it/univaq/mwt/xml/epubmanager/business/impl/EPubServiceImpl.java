package it.univaq.mwt.xml.epubmanager.business.impl;

import it.univaq.mwt.xml.epubmanager.business.BusinessException;
import it.univaq.mwt.xml.epubmanager.business.EPubService;
import it.univaq.mwt.xml.epubmanager.business.StAXService;
import it.univaq.mwt.xml.epubmanager.business.model.EpubCss;
import it.univaq.mwt.xml.epubmanager.business.model.EpubImage;
import it.univaq.mwt.xml.epubmanager.business.model.EpubXhtml;
import it.univaq.mwt.xml.epubmanager.business.model.Metadata;
import it.univaq.mwt.xml.epubmanager.common.utility.Config;
import it.univaq.mwt.xml.epubmanager.common.utility.DirectoryUtil;
import it.univaq.mwt.xml.epubmanager.common.utility.XMLUtil;
import it.univaq.mwt.xml.epubmanager.common.utility.ZipUtil;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EPubServiceImpl implements EPubService {

    @Autowired
    private StAXService StAXService;

    @Autowired
    private DataSource dataSource;

    private static final String UPLOAD_DIR = Config.getSetting("uploadDirectory");

    @Override
    public long startEpub(Metadata metadata) throws BusinessException {

        System.out.println("Business logic: startEpub");

        long result = Long.parseLong(metadata.getIdentifier());

        // creo una cartella con l'isbn dell'epub
        DirectoryUtil.mkdir(UPLOAD_DIR, metadata.getIdentifier());

        Connection con = null;
        PreparedStatement st = null;
        try {
            con = dataSource.getConnection();
            String sql = "insert into metadata(identifier, title, language) values (?, ?, ?)";
            st = con.prepareStatement(sql);
            st.setString(1, metadata.getIdentifier());
            st.setString(2, metadata.getTitle());
            st.setString(3, metadata.getLanguage());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new BusinessException(e);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    throw new BusinessException(e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new BusinessException(e);
                }
            }
        }

        return result;
    }

    @Override
    public int addXHTML(long token, EpubXhtml epubXhtml) throws BusinessException {
        Connection con = null;
        PreparedStatement st = null;
        int result;

        try {
            con = dataSource.getConnection();
            String sql = "insert into xhtml(id, name, path, contentType, indice, type, epub) values (NULL, ?, ?, ?, ?, ?, ?)";
            st = con.prepareStatement(sql);
            st.setString(1, epubXhtml.getName());
            st.setString(2, epubXhtml.getPath());
            st.setString(3, epubXhtml.getContentType());
            st.setInt(4, epubXhtml.getIndex());
            st.setString(5, epubXhtml.getType());
            st.setString(6, Long.toString(token));
            result = st.executeUpdate();
        } catch (SQLException e) {
            throw new BusinessException(e);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    throw new BusinessException(e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new BusinessException(e);
                }
            }
        }

        if (result != 0) {
            //creating a temp file
            File temp = new File(UPLOAD_DIR + Long.toString(token) + File.separator + epubXhtml.getPath());
            try {
                //saving image to file
                FileUtils.writeByteArrayToFile(temp, epubXhtml.getFile());
                System.out.println("Upload dei file " + epubXhtml.getPath() + " avvenuto con successo!");
            } catch (IOException ex) {
                throw new BusinessException("Upload del file " + epubXhtml.getPath() + " fallito!", ex);
            }

            // controllo che il file sia valido
            boolean b = XMLUtil.validateXhtml(temp, true);
            System.out.println("ERRORI NELLA VALIDAZIONE? " + b);

            // cancello il file se ho errori di validazione
            if (b == true) {
                System.out.println("Cancello il file uplodato: non è valido!");
                temp.delete();
            }
            return result;
        } else {
            return 0;
        }
    }

    @Override
    public int addStylesheet(long token, EpubCss epubCss) throws BusinessException {
        Connection con = null;
        PreparedStatement st = null;
        int result;

        try {
            con = dataSource.getConnection();
            String sql = "insert into css(id, name, path, contentType, epub) values (NULL, ?, ?, ?, ?)";
            st = con.prepareStatement(sql);
            st.setString(1, epubCss.getName());
            st.setString(2, epubCss.getPath());
            st.setString(3, epubCss.getContentType());
            st.setString(4, Long.toString(token));
            result = st.executeUpdate();
        } catch (SQLException e) {
            throw new BusinessException(e);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    throw new BusinessException(e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new BusinessException(e);
                }
            }
        }

        if (result != 0) {
            //creating a temp file
            File temp = new File(UPLOAD_DIR + Long.toString(token) + File.separator + epubCss.getPath());
            try {
                //saving image to file
                FileUtils.writeByteArrayToFile(temp, epubCss.getFile());
                System.out.println("Upload del file " + epubCss.getPath() + " avvenuto con successo!");
            } catch (IOException ex) {
                throw new BusinessException("Upload del file " + epubCss.getPath() + " fallito!", ex);
            }
            return result;
        } else {
            return 0;
        }
    }

    @Override
    public int addImage(long token, EpubImage epubImage) throws BusinessException {
        Connection con = null;
        PreparedStatement st = null;
        int result;

        try {
            con = dataSource.getConnection();
            String sql = "insert into image(id, name, path, contentType, epub) values (NULL, ?, ?, ?, ?)";
            st = con.prepareStatement(sql);
            st.setString(1, epubImage.getName());
            st.setString(2, epubImage.getPath());
            st.setString(3, epubImage.getContentType());
            st.setString(4, Long.toString(token));
            result = st.executeUpdate();
        } catch (SQLException e) {
            throw new BusinessException(e);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    throw new BusinessException(e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new BusinessException(e);
                }
            }
        }

        if (result != 0) {
            //creating a temp file
            File temp = new File(UPLOAD_DIR + Long.toString(token) + File.separator + epubImage.getPath());
            try {
                //saving image to file
                FileUtils.writeByteArrayToFile(temp, epubImage.getFile());
                System.out.println("Upload del file " + epubImage.getPath() + " avvenuto con successo!");
            } catch (IOException ex) {
                throw new BusinessException("Upload del file " + epubImage.getPath() + " fallito!", ex);
            }
            return result;
        } else {
            return 0;
        }
    }

    @Override
    public void finalizeEpub(long token) throws BusinessException {

        String upload = UPLOAD_DIR + Long.toString(token) + File.separator;

        // creo la cartella META-INF
        DirectoryUtil.mkdir(upload, "META-INF");

        // creo il file toc.ncx
        StAXService.createTocNcxXMLFile(getEpubMetadataByPk(Long.toString(token)), findAllEpubXhtml(token), upload);

        // creo il file content.opf
        StAXService.createContentXMLFile(getEpubMetadataByPk(Long.toString(token)), findAllEpubXhtml(token), findAllEpubCss(token), findAllEpubImage(token), upload);

        // creo il file container.xml
        StAXService.createContainerXMLFile(upload + "META-INF" + File.separator);

        String uploadZip = UPLOAD_DIR + Long.toString(token) + File.separator;
        ZipUtil.createArchiveFile(uploadZip, Long.toString(token));
    }

    @Override
    public List<EpubXhtml> findAllEpubXhtml(long epub) throws BusinessException {
        List<EpubXhtml> result = new ArrayList<EpubXhtml>();
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            st = con.prepareStatement("select * from xhtml where epub = ?");
            st.setString(1, Long.toString(epub));
            rs = st.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String path = rs.getString("path");
                String contentType = rs.getString("contentType");
                int indice = rs.getInt("indice");
                String type = rs.getString("type");
                EpubXhtml xhtml = new EpubXhtml(id, name, path, null, indice, contentType, Long.toBinaryString(epub), type);
                result.add(xhtml);
            }
        } catch (SQLException e) {
            throw new BusinessException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }

        }
        return result;
    }

    @Override
    public List<EpubCss> findAllEpubCss(long epub) throws BusinessException {
        List<EpubCss> result = new ArrayList<EpubCss>();
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            st = con.prepareStatement("select * from css where epub = ?");
            st.setString(1, Long.toString(epub));
            rs = st.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String path = rs.getString("path");
                String contentType = rs.getString("contentType");
                EpubCss css = new EpubCss(id, name, path, null, contentType, Long.toString(epub));
                result.add(css);
            }
        } catch (SQLException e) {
            throw new BusinessException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }

        }
        return result;
    }

    @Override
    public List<EpubImage> findAllEpubImage(long epub) throws BusinessException {
        List<EpubImage> result = new ArrayList<EpubImage>();
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            st = con.prepareStatement("select * from image where epub = ?");
            st.setString(1, Long.toString(epub));
            rs = st.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String path = rs.getString("path");
                String contentType = rs.getString("contentType");
                EpubImage image = new EpubImage(id, name, path, null, contentType, Long.toString(epub));
                result.add(image);
            }
        } catch (SQLException e) {
            throw new BusinessException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }

        }
        return result;
    }
    
    @Override
    public Metadata getEpubMetadataByPk(String pk) throws BusinessException {
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        Metadata result = null;
        try {
            con = dataSource.getConnection();
            st = con.prepareStatement("select * from metadata where identifier = ?");
            st.setString(1, pk);
            rs = st.executeQuery();
            if (rs.next()) {
                String identifier = rs.getString("identifier");
                String title = rs.getString("title");
                String language = rs.getString("language");
                result = new Metadata(identifier, title, language);
            }
        } catch (SQLException e) {
            throw new BusinessException(e);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }

        }
        return result;
    }

    @Override
    public EpubXhtml getEpubXhtmlByPk(String pk) {
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        EpubXhtml result = null;
        try {
            con = dataSource.getConnection();
            st = con.prepareStatement("select * from xhtml where id = ?");
            st.setString(1, pk);
            rs = st.executeQuery();
            if (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String path = rs.getString("path");
                String contentType = rs.getString("contentType");
                int indice = rs.getInt("indice");
                String type = rs.getString("type");
                String epub = rs.getString("epub");
                result = new EpubXhtml(id, name, path, null, indice, contentType, epub, type);
            }
        } catch (SQLException e) {
            throw new BusinessException(e);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }

        }
        return result;
    }

    @Override
    public void updateEpubXhtml(EpubXhtml epubXhtml) {
        Connection con = null;
        PreparedStatement st = null;
        try {
            con = dataSource.getConnection();
            st = con.prepareStatement("update xhtml set name=?, path=?, contentType=?, indice=?, type=? where id=?");
            st.setString(1, epubXhtml.getName());
            st.setString(2, epubXhtml.getPath());
            st.setString(3, epubXhtml.getContentType());
            st.setInt(4, epubXhtml.getIndex());
            st.setString(5, epubXhtml.getType());
            st.setString(6, epubXhtml.getId());
            st.executeUpdate();

        } catch (SQLException e) {
            throw new BusinessException(e);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }

        }
    }

    @Override
    public void deleteEpubXhtml(EpubXhtml epubXhtml) {
        Connection con = null;
        PreparedStatement st = null;
        int result;
        try {
            con = dataSource.getConnection();
            st = con.prepareStatement("delete from xhtml where id=?");
            st.setString(1, epubXhtml.getId());
            result = st.executeUpdate();
        } catch (SQLException e) {
            throw new BusinessException(e);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }

        }
        
        if (result > 0) {
            // rimuovo il file dal file system
            File file = new File(UPLOAD_DIR + epubXhtml.getEpub() + File.separator + epubXhtml.getPath());
            boolean delete = file.delete();
        }
    }

}
