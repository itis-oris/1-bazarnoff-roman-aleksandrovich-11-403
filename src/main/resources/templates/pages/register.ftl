<#import "../layout/base.ftl" as layout>

<@layout.base title="Регистрация">

    <div style="max-width: 700px; margin: 2rem auto;">
        <div class="form-box">
            <h1>Регистрация нового пользователя</h1>
            <p class="text-muted">Заполните форму для создания учётной записи на академической платформе</p>

            <#if error??>
                <div class="alert alert-error">
                    <strong>Ошибка регистрации:</strong> ${error}
                </div>
            </#if>

            <form method="POST" action="${contextPath}/register">
                <h3 style="margin-top: 1.5rem;">Учётные данные</h3>

                <div class="form-group">
                    <label>Электронная почта *</label>
                    <input type="email" name="email" value="${email!''}" required>
                    <small>Используйте университетскую почту</small>
                </div>

                <div class="form-group">
                    <label>Пароль *</label>
                    <input type="password" name="password" required minlength="6">
                    <small>Минимум 6 символов</small>
                </div>

                <div class="form-group">
                    <label>Подтверждение пароля *</label>
                    <input type="password" name="passwordConfirm" required minlength="6">
                </div>

                <h3 style="margin-top: 2rem;">Личная информация</h3>

                <div class="form-group">
                    <label>Полное имя *</label>
                    <input type="text" name="fullName" value="${fullName!''}" required>
                    <small>Фамилия Имя Отчество</small>
                </div>


                <div class="form-group">
                    <label>Университет</label>
                    <input type="text" name="university" value="${university!''}">
                </div>

                <div class="form-group">
                    <label>Факультет</label>
                    <input type="text" name="faculty" value="${faculty!''}">
                </div>

                <div class="form-group">
                    <label>Курс обучения</label>
                    <input type="number" name="course" value="${course!''}" min="1" max="6" placeholder="1-6">
                </div>

                <div style="margin-top: 2rem; padding-top: 1rem; border-top: 2px solid #e8e8e0;">
                    <button type="submit" class="btn btn-primary" style="width: 100%;">Зарегистрироваться</button>
                </div>
            </form>

            <div style="margin-top: 1.5rem; text-align: center;">
                <p class="text-muted">
                    Уже есть аккаунт? <a href="${contextPath}/login">Войти в систему</a>
                </p>
            </div>
        </div>
    </div>
</@layout.base>