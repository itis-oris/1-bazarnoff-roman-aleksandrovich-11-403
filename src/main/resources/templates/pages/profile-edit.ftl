<#import "../layout/base.ftl" as layout>

<@layout.base title="Редактирование профиля">

    <div class="breadcrumbs">
        <a href="${contextPath}/">Главная</a> <span>→</span>
        <a href="${contextPath}/profile">Мой профиль</a> <span>→</span>
        Редактирование
    </div>

    <div style="max-width: 800px; margin: 0 auto;">
        <div class="form-box">
            <h1>Редактирование профиля</h1>

            <#if error??>
                <div class="alert alert-error">
                    <strong>Ошибка:</strong> ${error}
                </div>
            </#if>

            <form method="POST" action="${contextPath}/profile/edit">


                <div class="form-group">
                    <label>Полное имя *</label>
                    <input type="text" name="fullName" value="${user.fullName}" required>
                </div>

                <div class="form-group">
                    <label>Университет</label>
                    <input type="text" name="university" value="${user.university!''}">
                </div>

                <div class="form-group">
                    <label>Факультет</label>
                    <input type="text" name="faculty" value="${user.faculty!''}">
                </div>

                <div class="form-group">
                    <label>Курс обучения</label>
                    <input type="number" name="course" value="${user.course!''}" min="1" max="6">
                </div>

                <div class="form-group">
                    <label>О себе</label>
                    <textarea name="about" rows="6">${user.about!''}</textarea>
                    <small>Расскажите о своих академических интересах, достижениях</small>
                </div>

                <div style="display: flex; gap: 1rem; margin-top: 2rem;">
                    <button type="submit" class="btn btn-primary" style="flex: 1;">Сохранить изменения</button>
                    <a href="${contextPath}/profile" class="btn btn-secondary" style="flex: 1; text-align: center;">Отмена</a>
                </div>
            </form>
        </div>
    </div>

    </script>

</@layout.base>