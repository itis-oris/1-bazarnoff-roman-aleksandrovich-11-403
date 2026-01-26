<#import "../layout/base.ftl" as layout>

<@layout.base title="Профиль пользователя">

    <div class="breadcrumbs">
        <a href="${contextPath}/">Главная</a> <span>→</span>
        <#if isOwnProfile>
            Мой профиль
        <#else>
            Профиль пользователя
        </#if>
    </div>

    <div class="forum-box">
        <div class="forum-header">
            <div style="display: flex; justify-content: space-between; align-items: center;">

                <div style="width: 80px; height: 80px; background: #f8f8f5; border: 3px solid #d4d4c8; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 2.5rem;">
                    👤
                </div>

                <div>
                    <div style="display: flex; align-items: center; gap: 1rem;">
                        <div class="forum-title">${user.fullName}</div>
                        <#if user.role?? && user.role.name() == "MODERATOR">
                            <span style="background: #dc3545; color: white; padding: 0.25rem 0.75rem; border-radius: 4px; font-size: 0.85rem; font-weight: 600;">
                                МОДЕРАТОР
                            </span>
                        </#if>
                    </div>
                    <div class="forum-meta">Участник с ${user.registrationDate}</div>
                </div>

                <#if isOwnProfile>
                    <a href="${contextPath}/profile/edit" class="btn btn-secondary">Редактировать профиль</a>
                <#else>
                    <div style="width: 200px;"></div>
                </#if>
            </div>
        </div>

        <div class="forum-body">
            <div style="display: grid; grid-template-columns: 1fr 2fr; gap: 2rem;">
                <div>
                    <h3>Репутация</h3>
                    <div style="text-align: center; padding: 2rem; background: #f8f8f5; border: 2px solid #d4d4c8; border-radius: 4px;">
                        <div style="font-size: 3rem; font-weight: bold; color: #1e3a5f;">
                            ${user.reputationPoints}
                        </div>
                        <div class="text-muted">баллов</div>
                    </div>
                </div>

                <div>
                    <h3>Основная информация</h3>
                    <table style="border: none;">
                        <#if isOwnProfile>
                            <tr>
                                <td style="font-weight: bold; width: 180px; border: none;">Email:</td>
                                <td style="border: none;">${user.email}</td>
                            </tr>
                        </#if>
                        <#if user.university??>
                            <tr>
                                <td style="font-weight: bold; border: none;">Университет:</td>
                                <td style="border: none;">${user.university}</td>
                            </tr>
                        </#if>
                        <#if user.faculty??>
                            <tr>
                                <td style="font-weight: bold; border: none;">Факультет:</td>
                                <td style="border: none;">${user.faculty}</td>
                            </tr>
                        </#if>
                        <#if user.course??>
                            <tr>
                                <td style="font-weight: bold; border: none;">Курс:</td>
                                <td style="border: none;">${user.course}</td>
                            </tr>
                        </#if>
                    </table>
                </div>
            </div>

            <#if user.about??>
                <div style="margin-top: 2rem; padding-top: 2rem; border-top: 1px solid #e8e8e0;">
                    <h3>О себе</h3>
                    <p style="line-height: 1.8;">${user.about}</p>
                </div>
            </#if>
        </div>
    </div>

    <#if isOwnProfile>
        <h2>Быстрые действия</h2>

        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 1rem;">
            <a href="${contextPath}/requests/create" style="text-decoration: none;">
                <div class="forum-box" style="transition: transform 0.2s;">
                    <div class="forum-body" style="text-align: center;">
                        <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">❓</div>
                        <h3 style="margin: 0; color: #1e3a5f;">Задать вопрос</h3>
                        <p class="text-muted" style="margin-top: 0.5rem;">Создать новый запрос помощи</p>
                    </div>
                </div>
            </a>

            <a href="${contextPath}/materials/upload" style="text-decoration: none;">
                <div class="forum-box" style="transition: transform 0.2s;">
                    <div class="forum-body" style="text-align: center;">
                        <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">📤</div>
                        <h3 style="margin: 0; color: #1e3a5f;">Загрузить материал</h3>
                        <p class="text-muted" style="margin-top: 0.5rem;">Поделиться учебными материалами</p>
                    </div>
                </div>
            </a>

            <a href="${contextPath}/requests?status=OPEN" style="text-decoration: none;">
                <div class="forum-box" style="transition: transform 0.2s;">
                    <div class="forum-body" style="text-align: center;">
                        <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">💬</div>
                        <h3 style="margin: 0; color: #1e3a5f;">Помочь другим</h3>
                        <p class="text-muted" style="margin-top: 0.5rem;">Ответить на открытые вопросы</p>
                    </div>
                </div>
            </a>
        </div>
    </#if>

    <div style="margin-bottom: 1rem; display: flex; justify-content: flex-end;">
        <#if isOwnProfile>
            <a href="${contextPath}/profile/subjects" class="btn btn-success" style="background: #85bc6f; border-color: #85bc6f;">
                ➕ Добавить предмет
            </a>
        </#if>
    </div>

    <h2 style="margin-top: 2rem;">
        📚
        <#if isOwnProfile>
            Ваши предметы
        <#else>
            Предметы пользователя ${user.fullName}
        </#if>
        (${userSubjects?size})
    </h2>

    <#if userSubjects?size == 0>
        <div class="alert alert-info">
            <strong>
                <#if isOwnProfile>
                    У вас пока нет предметов.
                <#else>
                    У пользователя ${user.fullName} пока нет предметов.
                </#if>
            </strong>
            <#if isOwnProfile>
                <a href="${contextPath}/profile/subjects">Добавьте первый предмет</a>!
            </#if>
        </div>
    <#else>
        <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 1rem;">
            <#list userSubjects as item>
                <div style="border: 1px solid #ddd; border-radius: 8px; padding: 1rem; background: #f8f9fa;">
                    <div style="font-weight: bold; margin-bottom: 0.5rem;">
                        <a href="${contextPath}/subjects/${item.subject.id}">${item.subject.name}</a>
                    </div>

                    <div style="display: flex; gap: 0.5rem; flex-wrap: wrap; margin-top: 0.5rem;">
                        <#if item.userSubject.proficiencyLevel == "LEARNING">
                            <span class="badge" style="background: #5b9bd5; color: white; font-size: 0.8rem;">📘 Изучаю</span>
                        <#elseif item.userSubject.proficiencyLevel == "KNOWS">
                            <span class="badge" style="background: #70ad47; color: white; font-size: 0.8rem;">📗 Знаю</span>
                        <#elseif item.userSubject.proficiencyLevel == "EXPERT">
                            <span class="badge" style="background: #c9302c; color: white; font-size: 0.8rem;">📕 Эксперт</span>
                        </#if>

                        <#if item.userSubject.canHelp>
                            <span class="badge" style="background: #70ad47; color: white; font-size: 0.8rem;">✓ Помогаю</span>
                        </#if>
                    </div>

                    <#if item.subject.faculty??>
                        <div class="text-muted text-small" style="margin-top: 0.5rem;">
                            ${item.subject.faculty}
                        </div>
                    </#if>

                    <#if item.subject.description?? && item.subject.description?length gt 0>
                        <div class="text-muted text-small" style="margin-top: 0.5rem;">
                            ${item.subject.description}
                        </div>
                    </#if>
                </div>
            </#list>
        </div>

        <#if userSubjects?size gt 6>
            <div style="text-align: center; margin-top: 1rem;">
                <a href="${contextPath}/profile/subjects">Показать все предметы →</a>
            </div>
        </#if>
    </#if>

</@layout.base>