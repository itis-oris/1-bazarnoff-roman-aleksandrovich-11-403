function toggleFileInput() {
    const materialType = document.getElementById('materialTypeSelect').value;
    const fileInput = document.getElementById('fileInput');
    const videoLinkInput = document.getElementById('videoLinkInput');

    if (materialType === 'VIDEO_LINK') {
        fileInput.style.display = 'none';
        videoLinkInput.style.display = 'block';
        fileInput.querySelector('input[type="file"]').removeAttribute('required');
        videoLinkInput.querySelector('input[type="text"]').setAttribute('required', 'required');
    } else {
        fileInput.style.display = 'block';
        videoLinkInput.style.display = 'none';
        fileInput.querySelector('input[type="file"]').setAttribute('required', 'required');
        videoLinkInput.querySelector('input[type="text"]').removeAttribute('required');
    }
}

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', function() {
    toggleFileInput();
});