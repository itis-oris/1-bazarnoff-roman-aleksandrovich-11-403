<#import "../layout/base.ftl" as layout>

<@layout.base title="Вход в систему">

    <div style="max-width: 600px; margin: 2rem auto;">
        <div class="form-box">
            <h1>Вход в систему</h1>
            <p class="text-muted">Введите свои учётные данные для доступа к платформе</p>

            <#if error??>
                <div class="alert alert-error">
                    <strong>Ошибка входа:</strong> ${error}
                </div>
            </#if>

            <form method="POST" action="${contextPath}/login">
                <div class="form-group">
                    <label>Электронная почта</label>
                    <input type="email" name="email" value="${email!''}" required autofocus placeholder="student@university.ru">
                </div>

                <div class="form-group">
                    <label>Пароль</label>
                    <input type="password" name="password" required placeholder="Введите пароль">
                </div>

                <button type="submit" class="btn btn-primary" style="width: 100%;">Войти</button>
            </form>

            <div style="margin-top: 1.5rem; padding-top: 1.5rem; border-top: 2px solid #e8e8e0; text-align: center;">
                <p class="text-muted">
                    Ещё не зарегистрированы? <a href="${contextPath}/register">Создать аккаунт</a>
                </p>
            </div>
        </div>
    </div>

</@layout.base>