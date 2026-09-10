package com.example.lms.submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
@Service
public class SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;

    public Submission submitAssignmentWithFile(Submission submission, MultipartFile file) throws IOException {
        // حفظ الملف في المسار المناسب
        String filePath = saveFile(file);
        submission.setFilePath(filePath);  // تعيين مسار الملف
        return submissionRepository.save(submission);  // حفظ التقديم
    }
    private String saveFile(MultipartFile file) throws IOException {
        String uploadDir = "uploads/"; // المسار الأساسي لحفظ الملفات
        Path uploadPath = Paths.get(uploadDir);

        // إنشاء المجلد إذا لم يكن موجودًا
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // نسخ الملف إلى المسار المطلوب
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();  // إرجاع المسار الكامل للملف
    }

    public Submission submitAssignment(Submission submission) {
        return submissionRepository.save(submission);
    }

    public List<Submission> getSubmissionsByAssignmentId(String assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    public byte[] getSubmissionFileContent(Long submissionId) throws IOException {
        Submission submission = submissionRepository.findById(submissionId).orElseThrow(() ->
                new RuntimeException("Submission not found with ID: " + submissionId));

        Path filePath = Paths.get(submission.getFilePath());
        if (!Files.exists(filePath)) {
            throw new RuntimeException("File not found on server");
        }

        return Files.readAllBytes(filePath);
    }

    public Submission getSubmissionById(Long submissionId) {
        return submissionRepository.findById(submissionId).orElseThrow(() ->
                new RuntimeException("Submission not found with ID: " + submissionId));
    }


    /*
    public Submission addFeedback(Long submissionId, String feedback) {
        Submission submission = submissionRepository.findById(submissionId).orElse(null);
        if (submission != null) {
            submission.setFeedback(feedback); // إضافة الملاحظات
            return submissionRepository.save(submission); // حفظ التغيير
        }
        return null;
    }
     */
    public Submission addFeedback(Long submissionId, String feedback) {
        Submission submission = submissionRepository.findById(submissionId).orElseThrow(() ->
                new IllegalArgumentException("Submission not found with ID: " + submissionId));
        submission.setFeedback(feedback);
        return submissionRepository.save(submission);
    }
    public Submission gradeAssignment(Long submissionId, Long grade) {
        // 1. العثور على التقديم
        Submission submission = submissionRepository.findById(submissionId).orElseThrow(() ->
                new IllegalArgumentException("Submission not found with ID: " + submissionId));

        // 2. تعيين الدرجة (نخزنها كـ String)
        submission.setGrade(grade);

        // 3. حفظ التقديم بعد التعديل
        return submissionRepository.save(submission);
    }

    public List<Submission> getSubmissionsByAssignmentIdAndStudentId(String assignmentId, String studentId) {
        return submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId);
    }
}

