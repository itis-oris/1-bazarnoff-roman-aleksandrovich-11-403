<#macro base title>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title!"StudyHelper"} - Академический портал</title>
    <link rel="stylesheet" href="${contextPath}/static/css/style.css">
</head>
<body>
<div class="header">
    <div class="header-container">
        <a href="${contextPath}/" class="logo">🎓 StudyHelper</a>
        <nav class="nav">
            <#if user??>
                <a href="${contextPath}/profile">Мой профиль</a>
                <a href="${contextPath}/subjects">Дисциплины</a>
                <a href="${contextPath}/requests">Форум вопросов</a>
                <a href="${contextPath}/materials">Библиотека</a>
                <a href="${contextPath}/logout">Выход</a>
            <#else>
                <a href="${contextPath}/login">Вход</a>
                <a href="${contextPath}/register">Регистрация</a>
            </#if>
        </nav>
    </div>
</div>

<div class="container">
    <#nested>
</div>

<div class="footer">
    <p>© 2025 StudyHelper — Академическая платформа взаимопомощи студентов</p>
</div>

</body>
</html>
</#macro>