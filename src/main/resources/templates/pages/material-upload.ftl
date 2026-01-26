<#import "../layout/base.ftl" as layout>

<@layout.base title="Загрузить материал">

    <div class="breadcrumbs">
        <a href="${contextPath}/">Главная</a> <span>→</span>
        <a href="${contextPath}/materials">Библиотека материалов</a> <span>→</span>
        Загрузить материал
    </div>

    <div style="max-width: 800px; margin: 0 auto;">
        <div class="form-box">
            <h1>Загрузить учебный материал</h1>
            <p class="text-muted">Поделитесь своими конспектами, презентациями или другими материалами с другими студентами</p>

            <#if error??>
                <div class="alert alert-error">
                    <strong>Ошибка:</strong> ${error}
                </div>
            </#if>

            <form method="POST" action="${contextPath}/materials/upload" enctype="multipart/form-data">
                <div class="form-group">
                    <label>Дисциплина *</label>
                    <select name="subjectID" required>
                        <option value="">Выберите дисциплину</option>
                        <#list subjects as subject>
                            <option value="${subject.id}" <#if subjectID?? && subjectID == subject.id?string>selected</#if>>
                                ${subject.name}
                            </option>
                        </#list>
                    </select>
                    <small>К какой дисциплине относится материал</small>
                </div>

                <div class="form-group">
                    <label>Название материала *</label>
                    <input type="text" name="title" value="${title!''}" required placeholder="Конспект по теме...">
                    <small>Краткое, но информативное название</small>
                </div>

                <div class="form-group">
                    <label>Описание материала</label>
                    <textarea name="description" rows="4" placeholder="Что содержит материал, какие темы раскрыты...">${description!''}</textarea>
                    <small>Дополнительная информация о содержании</small>
                </div>

                <div class="form-group">
                    <label>Тип материала *</label>
                    <select name="materialType" id="materialTypeSelect" required onchange="toggleFileInput()">
                        <option value="">Выберите тип</option>
                        <option value="NOTES" <#if materialType?? && materialType == "NOTES">selected</#if>>📝 Конспект</option>
                        <option value="SUMMARY" <#if materialType?? && materialType == "SUMMARY">selected</#if>>📄 Краткий вывод</option>
                        <option value="PRESENTATION" <#if materialType?? && materialType == "PRESENTATION">selected</#if>>📊 Презентация</option>
                        <option value="VIDEO_LINK" <#if materialType?? && materialType == "VIDEO_LINK">selected</#if>>🎥 Ссылка на видео</option>
                    </select>
                </div>

                <div class="form-group" id="fileInput">
                    <label>Файл материала *</label>
                    <input type="file" name="file" accept=".pdf,.doc,.docx,.ppt,.pptx,.txt">
                    <small>Максимальный размер файла: 10 МБ. Форматы: PDF, DOC, DOCX, PPT, PPTX, TXT</small>
                </div>

                <div class="form-group" id="videoLinkInput" style="display: none;">
                    <label>Ссылка на видео *</label>
                    <input type="text" name="videoUrl" placeholder="https://www.youtube.com/watch?v=...">
                    <small>Вставьте ссылку на YouTube, Rutube или другой видеохостинг</small>
                </div>

                <div class="alert alert-info" style="margin-top: 1.5rem;">
                    <strong>📌 Рекомендации:</strong>
                    <ul style="margin: 0.5rem 0 0 1.5rem;">
                        <li>Убедитесь, что материал качественный и полезный для других студентов</li>
                        <li>Проверьте файл на наличие ошибок перед загрузкой</li>
                        <li>Не загружайте материалы, защищённые авторским правом</li>
                    </ul>
                </div>

                <div style="display: flex; gap: 1rem; margin-top: 2rem;">
                    <button type="submit" class="btn btn-primary" style="flex: 1;">Загрузить материал</button>
                    <a href="${contextPath}/materials" class="btn btn-secondary" style="flex: 1; text-align: center;">Отмена</a>
                </div>
            </form>
        </div>
    </div>

    <script src="${contextPath}/static/js/uploadFile.js">
    </script>

</@layout.base>