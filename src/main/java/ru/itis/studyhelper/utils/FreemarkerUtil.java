    package ru.itis.studyhelper.utils;

    import freemarker.template.*;
    import jakarta.servlet.ServletContext;
    import jakarta.servlet.http.HttpSession;
    import lombok.extern.slf4j.Slf4j;

    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import ru.itis.studyhelper.models.User;

    import java.io.IOException;
    import java.io.Writer;
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.HashMap;
    import java.util.Map;

    @Slf4j
    public class FreemarkerUtil {
        private static Configuration configuration;

        public static void init() {
            configuration = new Configuration(Configuration.VERSION_2_3_21);
            configuration.setClassForTemplateLoading (FreemarkerUtil.class, "/templates");
            configuration.setDefaultEncoding("UTF-8");

            configuration.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_21) {

                @Override
                public TemplateModel wrap(Object obj) throws TemplateModelException {
                    if (obj instanceof LocalDateTime dt) {
                        return new SimpleScalar(dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    }
                    return super.wrap(obj);
                }
            });
        }

        public static void render(String templateName, Map<String, Object> data,
                                  HttpServletResponse response) throws IOException {

            response.setContentType("text/html;charset=UTF-8");

            try (Writer wr = response.getWriter()) {
                Template template = configuration.getTemplate(templateName);
                template.process(data, wr);

            } catch (TemplateException e) {
                e.printStackTrace();
            }
        }


        public static Map<String, Object> createModel(HttpServletRequest req) {
            Map<String, Object> model = new HashMap<>();
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute("user");
            model.put("contextPath", req.getContextPath());
            model.put("req", req);

            model.put("user", user);

            return model;

        }
    }
