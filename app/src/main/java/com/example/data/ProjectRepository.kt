package com.example.data

import com.example.model.AIGenerationItem
import com.example.model.AITemplate
import com.example.model.AudioItem
import com.example.model.Project
import com.example.model.VoiceProfile
import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val dao: ProjectDao) {
    val allProjects: Flow<List<Project>> = dao.getAllProjects()
    val allGenerations: Flow<List<AIGenerationItem>> = dao.getAllGenerations()

    suspend fun insertProject(project: Project): Long = dao.insertProject(project)
    suspend fun updateProject(project: Project) = dao.updateProject(project)
    suspend fun deleteProject(project: Project) = dao.deleteProject(project)

    suspend fun insertGeneration(item: AIGenerationItem): Long = dao.insertGeneration(item)
    suspend fun deleteGeneration(id: Long) = dao.deleteGeneration(id)

    // Mẫu AI CapCut thịnh hành (Trending AI Templates)
    fun getTemplates(): List<AITemplate> = listOf(
        AITemplate(
            id = "tmpl_1",
            title = "Giật Giật Phonk TikTok",
            category = "Thịnh hành TikTok",
            aspectRatio = "9:16",
            duration = "15s",
            bpm = 135,
            musicTitle = "Neon Velocity (Drift Bass Remix)",
            tags = listOf("Hành động", "Chuyển cảnh nhanh", "Bass Drop"),
            description = "Tự động khớp nhịp bass giật giật theo nhạc với hiệu ứng lóe sáng sắc độ chuẩn TikTok."
        ),
        AITemplate(
            id = "tmpl_2",
            title = "Vlog Du Lịch Điện Ảnh",
            category = "Phong cách sống",
            aspectRatio = "9:16",
            duration = "22s",
            bpm = 95,
            musicTitle = "Chân trời rực rỡ (Acoustic Chill)",
            tags = listOf("Du lịch", "Quay chậm Slow-Mo", "Màu phim 35mm"),
            description = "Tăng giảm tốc độ mượt mà với hạt phim 35mm điện ảnh và hiệu ứng lóa sáng nhẹ nhàng."
        ),
        AITemplate(
            id = "tmpl_3",
            title = "Giới Thiệu Sản Phẩm Sang Trọng",
            category = "Quảng cáo bán hàng",
            aspectRatio = "9:16",
            duration = "18s",
            bpm = 120,
            musicTitle = "Deep Lounge Luxury Beat",
            tags = listOf("Thương mại", "Chữ 3D xoay", "Ánh sáng Studio"),
            description = "Trưng bày sản phẩm chuyên nghiệp với chữ 3D xoay vòng, huy hiệu giá và bóng đổ phản chiếu."
        ),
        AITemplate(
            id = "tmpl_4",
            title = "Băng Từ VHS Thập Niên 90",
            category = "Phong cách cổ điển",
            aspectRatio = "4:5",
            duration = "14s",
            bpm = 88,
            musicTitle = "Băng cát-sét đêm muộn",
            tags = listOf("Vintage", "Hoài niệm", "Nhiễu sóng"),
            description = "Đường quét màn hình CRT chân thực, lớp phủ mã thời gian và méo hình băng từ cổ điển."
        ),
        AITemplate(
            id = "tmpl_5",
            title = "Mở Đầu Phim Anime Vietsub",
            category = "Hoạt hình phong cách",
            aspectRatio = "16:9",
            duration = "25s",
            bpm = 150,
            musicTitle = "Nhạc mở đầu Anime Hào Khí",
            tags = listOf("Anime", "Thu phóng động", "Tia lửa điện"),
            description = "Hiệu ứng đường tốc độ cao, phụ đề động Vietsub nghệ thuật và hiệu ứng bùng nổ hạt ánh sáng."
        )
    )

    // Kho 200+ Giọng đọc AI tự nhiên (Bắc, Trung, Nam & Quốc tế)
    fun getVoiceProfiles(): List<VoiceProfile> = listOf(
        VoiceProfile("v_vn_1", "Minh Trí (Hà Nội)", "MC Thời sự & Phóng sự", "Tiếng Việt (Bắc)", "Nam", "Trang trọng & Truyền cảm", "Chào mừng quý vị và các bạn đã quay trở lại với bản tin đặc biệt ngày hôm nay."),
        VoiceProfile("v_vn_2", "Ngọc Mai (Sài Gòn)", "Quảng cáo & TikTok Hype", "Tiếng Việt (Nam)", "Nữ", "Ngọt ngào & Sôi động", "Dừng lại 3 giây thôi! Bí quyết sáng tạo video triệu view đang ở ngay đây nè!"),
        VoiceProfile("v_vn_3", "Huy Review (Phim)", "Review Phim & Kịch tính", "Tiếng Việt (Bắc)", "Nam", "Kịch tính & Lôi cuốn", "Bộ phim mở đầu bằng một bí ẩn kinh hoàng mà không một ai có thể ngờ tới..."),
        VoiceProfile("v_vn_4", "Thảo Vy (Đà Nẵng)", "Podcast & Chữa lành", "Tiếng Việt (Trung)", "Nữ", "Dịu dàng & Ấm áp", "Hãy hít một hơi thật sâu, thả lỏng tâm trí và để những âu lo dần tan biến."),
        VoiceProfile("v_vn_5", "Hoàng Nam (Lồng tiếng)", "Vietsub Phim Chiếu Rạp", "Tiếng Việt (Toàn quốc)", "Nam", "Trầm ấm & Quyền lực", "Trong bóng tối bao trùm, ánh sáng hy vọng là vũ khí duy nhất còn sót lại."),
        VoiceProfile("v_vn_6", "Marcus Hype (US)", "Quảng cáo Quốc tế", "English (US)", "Male", "Mạnh mẽ & Cuốn hút", "Stop scrolling! Here is the revolutionary AI workflow you need."),
        VoiceProfile("v_vn_7", "Aria Cinema (US)", "Trailer Phim Điện ảnh", "English (US)", "Female", "Huyền bí & Sâu lắng", "In a world sculpted by shadows, truth was the only rebellion."),
        VoiceProfile("v_vn_8", "Kenji Neon (Tokyo)", "Anime & Gaming", "Japanese (Tokyo)", "Male", "Hào hùng & Nhiệt huyết", "Saiko no sekai o misete yaru zo! Kore ga ore no chikara da!"),
        VoiceProfile("v_vn_9", "Camille Paris (Pháp)", "Thời trang & Xa xỉ", "French (Paris)", "Female", "Quý phái & Tinh tế", "Une elegance intemporelle pour chaque instant inoubliable."),
        VoiceProfile("v_vn_10", "Mei Harmonious (Bắc Kinh)", "Thuyết minh Cổ trang", "Mandarin (Beijing)", "Female", "Thanh thoát & Nhẹ nhàng", "Zhe shi dongtian zui mei de shun jian, qing ting feng de sheng yin.")
    )

    // Kho Âm thanh & Nhạc miễn phí bản quyền (Royalty-free Audio & SFX)
    fun getRoyaltyFreeAudio(): List<AudioItem> = listOf(
        AudioItem("m_1", "Hoàng Hôn Điện Tử (Cyber Beat)", "Synthwave Syndicate", "02:45", false, "Thịnh hành TikTok, Sôi động, Điện tử"),
        AudioItem("m_2", "Mưa Đêm Quán Cà Phê (Lo-Fi Chill)", "Coffee & Rain Studio", "03:10", false, "Thư giãn, Nhạc nền, Học tập"),
        AudioItem("m_3", "Trailer Khởi Đầu Huyền Thoại", "Apex Orchestral", "01:50", false, "Điện ảnh, Hùng tráng, Cao trào"),
        AudioItem("m_4", "Giai Điệu Trap Phố Thị", "BPM 140 Masters", "02:22", false, "Viral TikTok, Bass mạnh, Trend"),
        AudioItem("m_5", "Nắng Ấm Cao Nguyên (Acoustic)", "Warm Woods Trio", "02:05", false, "Vlog du lịch, Tươi vui, Ukulele"),
        AudioItem("s_1", "Vút Chuyển Cảnh Gió Nhanh", "Gói Chuyển Cảnh", "00:01", true, "SFX, Chuyển cảnh, Gió lướt"),
        AudioItem("s_2", "Âm Trầm Nổ Uy Lực (Sub Bass)", "Hiệu Ứng Điện Ảnh", "00:02", true, "SFX, Cú đấm, Rung chuyển"),
        AudioItem("s_3", "Tiếng Cười Khán Giả Trường Quay", "Hiệu Ứng Hài Hước", "00:04", true, "SFX, Hài hước, Phản ứng"),
        AudioItem("s_4", "Âm Thanh Thăng Cấp Chiến Thắng", "Trò Chơi Cổ Điển", "00:02", true, "SFX, Game, Chiến thắng"),
        AudioItem("s_5", "Tiếng Màn Trập Máy Ảnh Tách Tách", "Âm Thanh Studio", "00:01", true, "SFX, Chụp ảnh, Tách tách")
    )
}
