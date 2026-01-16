<#import "../layout/base.ftl" as layout>

<@layout.base title="Главная страница">

    <#if user??>
        <!-- Для авторизованных пользователей -->
        <div class="forum-box">
            <div class="forum-header">
                <div class="forum-title">Добро пожаловать, ${user.fullName}!</div>
                <div class="forum-meta">Ваша репутация: <strong>${user.reputationPoints}</strong> баллов</div>
            </div>
            <div class="forum-body">
                <p>Рады видеть вас на академической платформе взаимопомощи студентов.</p>
                <div style="margin-top: 1rem;">
                    <a href="${contextPath}/requests/create" class="btn btn-primary">Задать вопрос</a>
                    <a href="${contextPath}/requests" class="btn btn-secondary">Все вопросы</a>
                    <a href="${contextPath}/materials/upload" class="btn btn-secondary">Загрузить материал</a>
                </div>
            </div>
        </div>
    <#else>
        <!-- Для неавторизованных -->
        <div class="forum-box">
            <div class="forum-header">
                <div class="forum-title">🎓 StudyHelper — Академический портал</div>
                <div class="forum-meta">Платформа взаимопомощи студентов</div>
            </div>
            <div class="forum-body">
                <h2 style="border: none; margin-top: 0;">О платформе</h2>
                <p style="font-size: 1.05rem; line-height: 1.8;">
                    <strong>StudyHelper</strong> — это академическая платформа, где студенты помогают друг другу
                    в освоении учебных дисциплин, обмениваются знаниями и материалами.
                </p>

                <div class="alert alert-info" style="margin-top: 1.5rem;">
                    <strong>📌 Для начала работы:</strong><br>
                    <a href="${contextPath}/register">Зарегистрируйтесь</a> или
                    <a href="${contextPath}/login">войдите в систему</a>
                </div>
            </div>
        </div>
    </#if>

    <!-- Статистика -->
    <div class="stats-bar">
        <div class="stat-item">
            <div class="stat-value">📚</div>
            <div class="stat-label">Академические дисциплины</div>
        </div>
        <div class="stat-item">
            <div class="stat-value">❓</div>
            <div class="stat-label">Вопросы и ответы</div>
        </div>
        <div class="stat-item">
            <div class="stat-value">📖</div>
            <div class="stat-label">Учебные материалы</div>
        </div>
        <div class="stat-item">
            <div class="stat-value">👥</div>
            <div class="stat-label">Активные студенты</div>
        </div>
    </div>

    <!-- Возможности -->
    <h2>Возможности платформы</h2>

    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 1.5rem;">
        <div class="forum-box">
            <div class="forum-body">
                <h3>🙋 Задавайте вопросы</h3>
                <p class="text-muted">
                    Столкнулись с трудностями в учёбе? Опишите проблему, и другие студенты помогут вам разобраться.
                </p>
            </div>
        </div>

        <div class="forum-box">
            <div class="forum-body">
                <h3>💬 Отвечайте на вопросы</h3>
                <p class="text-muted">
                    Поделитесь своими знаниями, помогите другим студентам и зарабатывайте репутацию.
                </p>
            </div>
        </div>

        <div class="forum-box">
            <div class="forum-body">
                <h3>📚 Делитесь материалами</h3>
                <p class="text-muted">
                    Загружайте конспекты, презентации и другие учебные материалы для общего пользования.
                </p>
            </div>
        </div>
    </div>

</@layout.base>