package com.duong.mycase41.controller;

import com.duong.mycase41.model.*;
import com.duong.mycase41.model.DTO.formUser.MinistryForm;
import com.duong.mycase41.model.DTO.formUser.StudentForm;
import com.duong.mycase41.model.DTO.formUser.TeacherForm;
import com.duong.mycase41.service.appuser.IAppUserService;
import com.duong.mycase41.service.classes.ClassesService;
import com.duong.mycase41.service.gender.IGenderService;
import com.duong.mycase41.service.ministry.IMinistryService;
import com.duong.mycase41.service.student.IStudentService;
import com.duong.mycase41.service.subject.ISubjectService;
import com.duong.mycase41.service.teacher.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
public class AdminController {
    @Autowired
    private IGenderService genderService;
    @Autowired
    private Environment environment;
    @Autowired
    private ClassesService classesService;

    @Autowired
    private IAppUserService appUserService;

    //-----------CLASSES--------------
    @GetMapping("/classes")
    public ResponseEntity<Page<Classes>> getAllClasses(@RequestParam(name = "c") Optional<String> c, @PageableDefault(value = 3) Pageable pageable) {
        Page<Classes> classes;
        if (!c.isPresent()) {
            classes = classesService.findAll(pageable);
        } else {
            classes = classesService.findAllByNameContaining(c.get(), pageable);
        }
        return new ResponseEntity<>(classes, HttpStatus.OK);

    }

    @PostMapping("/classes")
    public ResponseEntity<Classes> createClass(@ModelAttribute Classes classes) {
        return new ResponseEntity<>(classesService.save(classes), HttpStatus.CREATED);
    }

    @DeleteMapping("/classes/{id}")
    public ResponseEntity<Classes> deleteClasses(@PathVariable Long id) {
        Optional<Classes> classesOptional = classesService.findById(id);
        if (!classesOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        classesService.remove(id);
        return new ResponseEntity<>(classesOptional.get(), HttpStatus.OK);
    }

    @PostMapping("/classes/edit/{id}")
    public ResponseEntity<Classes> editClasses(@PathVariable Long id, @ModelAttribute Classes classes) {
        Optional<Classes> classesOptional = classesService.findById(id);
        if (!classesOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String name = classes.getName();
        Classes newClass = new Classes(name);
        newClass.setId(id);
        classesService.save(newClass);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //-----------SUBJECT--------------
    @Autowired
    ISubjectService subjectService;

    @GetMapping("/subject")
    public ResponseEntity<Iterable<AppSubject>> getAllSubject() {
        return new ResponseEntity<>(subjectService.findAll(), HttpStatus.OK);
    }

    @PostMapping("/subject")
    public ResponseEntity<AppSubject> createSubject(@ModelAttribute AppSubject subject) {
        return new ResponseEntity<>(subjectService.save(subject), HttpStatus.CREATED);
    }

    @DeleteMapping("/subject/{id}")
    public ResponseEntity<AppSubject> deleteSubject(@PathVariable Long id) {
        Optional<AppSubject> subjectOptional = subjectService.findById(id);
        if (!subjectOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        subjectService.remove(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/subject/edit/{id}")
    public ResponseEntity<AppSubject> editSubject(@PathVariable Long id, @ModelAttribute AppSubject appSubject) {
        Optional<AppSubject> subjectOptional = subjectService.findById(id);
        if (!subjectOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String name = appSubject.getName();
        AppSubject newSubject = new AppSubject(name);
        newSubject.setId(id);
        subjectService.save(newSubject);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //-----------TEACHER--------------
    @Autowired
    private ITeacherService teacherService;

    @GetMapping("/teachers")
    public ResponseEntity<Page<Teacher>> getAllTeacher(@RequestParam(name = "t") Optional<String> t, @PageableDefault(value = 8) Pageable pageable) {
        Page<Teacher> teachers;
        if (!t.isPresent()) {
            teachers = teacherService.findAll(pageable);
        } else {
            teachers = teacherService.findAllByFullNameContaining(t.get(), pageable);
        }
        return new ResponseEntity<>(teachers, HttpStatus.OK);

    }

    @GetMapping("/teachers/{id}")
    public ResponseEntity<Teacher> getByIdTeacher(@PathVariable Long id) {
        Optional<Teacher> teacherOptional = teacherService.findById(id);
        if (!teacherOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(teacherOptional.get(), HttpStatus.OK);
    }


    @PostMapping("/teachers")
    public ResponseEntity<Teacher> createTeacher(@ModelAttribute TeacherForm teacherForm) {
        MultipartFile file = teacherForm.getAvatar();
        String fileName = file.getOriginalFilename();
        String fileUpload = environment.getProperty("upload.path").toString();
        String userName = teacherForm.getUserName();
        String password = teacherForm.getPassword();
        Set<AppRole> roleSet = teacherForm.getRoleSet();
        String fullName = teacherForm.getFullName();
        String phoneNumber = teacherForm.getPhoneNumber();
        String email = teacherForm.getEmail();
        Gender gender = teacherForm.getGender();
        String dateOfBirth = teacherForm.getDateOfBirth();
        String address = teacherForm.getAddress();
        Set<Classes> classes = teacherForm.getClasses();
        try {
            FileCopyUtils.copy(teacherForm.getAvatar().getBytes(), new File(fileUpload + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        AppUser appUser = new AppUser(userName, password, roleSet);
        appUserService.save(appUser);
        Teacher teacher = new Teacher(appUser, fullName, phoneNumber, fileName, email, gender, dateOfBirth, address, classes);
        teacherService.save(teacher);
        return new ResponseEntity<>(teacher, HttpStatus.CREATED);
    }

    @DeleteMapping("/teachers/{id}")
    public ResponseEntity<Teacher> deleteTeacher(@PathVariable Long id) {
        Optional<Teacher> teacherOptional = teacherService.findById(id);
        if (!teacherOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        teacherService.remove(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("teachers/edit/{id}")
    public ResponseEntity<Teacher> editTeacher(@PathVariable Long id, @ModelAttribute TeacherForm teacherForm) {
        Optional<Teacher> teacherOptional = teacherService.findById(id);
        if (!teacherOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            MultipartFile file = teacherForm.getAvatar();
            String fileName = file.getOriginalFilename();
            String fileUpload = environment.getProperty("upload.path").toString();
            String userName = teacherForm.getUserName();
            String password = teacherForm.getPassword();
            Set<AppRole> roleSet = teacherForm.getRoleSet();
            String fullName = teacherForm.getFullName();
            String phoneNumber = teacherForm.getPhoneNumber();
            String email = teacherForm.getEmail();
            Gender gender = teacherForm.getGender();
            String dateOfBirth = teacherForm.getDateOfBirth();
            String address = teacherForm.getAddress();
            Set<Classes> classes = teacherForm.getClasses();
            try {
                FileCopyUtils.copy(teacherForm.getAvatar().getBytes(), new File(fileUpload + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            AppUser appUser = new AppUser(userName, password, roleSet);
            appUserService.save(appUser);
            Teacher teacher = new Teacher(appUser, fullName, phoneNumber, fileName, email, gender, dateOfBirth, address, classes);
            teacher.setId(id);
            teacherService.save(teacher);
            return new ResponseEntity<>(teacher, HttpStatus.OK);
        }
    }

    //-----------STUDENTS--------------
    @Autowired
    private IStudentService studentService;

    @GetMapping("/students")
    public ResponseEntity<Iterable<Student>> getAllStudent() {
        return new ResponseEntity<>(studentService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getByIdStudent(@PathVariable Long id) {
        Optional<Student> studentOptional = studentService.findById(id);
        if (!studentOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(studentOptional.get(), HttpStatus.OK);
    }

    @PostMapping("/students")
    public ResponseEntity<Student> createStudent(@ModelAttribute StudentForm studentForm) {
        MultipartFile file = studentForm.getAvatar();
        String fileName = file.getOriginalFilename();
        String fileUpload = environment.getProperty("upload.path").toString();
        String userName = studentForm.getUserName();
        String password = studentForm.getPassword();
        Set<AppRole> roleSet = studentForm.getRoleSet();
        String code = studentForm.getCode();
        String fullName = studentForm.getFullName();
        String phoneNumber = studentForm.getPhoneNumber();
        String email = studentForm.getEmail();
        Gender gender = studentForm.getGender();
        String dateOfBirth = studentForm.getDateOfBirth();
        String address = studentForm.getAddress();
        Classes classes = studentForm.getClasses();
        Tuition tuition = studentForm.getTuition();
        StatusStudent statusStudent = studentForm.getStatusStudent();
        try {
            FileCopyUtils.copy(studentForm.getAvatar().getBytes(), new File(fileUpload + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        AppUser appUser = new AppUser(userName, password, roleSet);
        appUserService.save(appUser);
        Student student = new Student(appUser, code, fullName, phoneNumber, fileName, email, gender, dateOfBirth, address, classes, tuition, statusStudent);
        studentService.save(student);
        return new ResponseEntity<>(student, HttpStatus.CREATED);
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable Long id) {
        Optional<Student> studentOptional = studentService.findById(id);
        if (!studentOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        studentService.remove(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/students/eidt/{id}")
    public ResponseEntity<Student> editStudent(@PathVariable Long id, @ModelAttribute StudentForm studentForm) {
        Optional<Student> studentOptional = studentService.findById(id);
        if (!studentOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            MultipartFile file = studentForm.getAvatar();
            String fileName = file.getOriginalFilename();
            String fileUpload = environment.getProperty("upload.path").toString();
            String userName = studentForm.getUserName();
            String password = studentForm.getPassword();
            Set<AppRole> roleSet = studentForm.getRoleSet();
            String code = studentForm.getCode();
            String fullName = studentForm.getFullName();
            String phoneNumber = studentForm.getPhoneNumber();
            String email = studentForm.getEmail();
            Gender gender = studentForm.getGender();
            String dateOfBirth = studentForm.getDateOfBirth();
            String address = studentForm.getAddress();
            Classes classes = studentForm.getClasses();
            Tuition tuition = studentForm.getTuition();
            StatusStudent statusStudent = studentForm.getStatusStudent();
            try {
                FileCopyUtils.copy(studentForm.getAvatar().getBytes(), new File(fileUpload + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            AppUser appUser = new AppUser(userName, password, roleSet);
            appUserService.save(appUser);
            Student student = new Student(appUser, code, fullName, phoneNumber, fileName, email, gender, dateOfBirth, address, classes, tuition, statusStudent);
            student.setId(id);
            studentService.save(student);
            return new ResponseEntity<>(student, HttpStatus.CREATED);
        }
    }

    //-----------MINISTRY--------------
    @Autowired
    private IMinistryService ministryService;

    @GetMapping("/ministries")
    public ResponseEntity<Iterable<Ministry>> getAllMinistry() {
        return new ResponseEntity<>(ministryService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/ministries/{id}")
    public ResponseEntity<Ministry> getByIdMinistry(@PathVariable Long id) {
        Optional<Ministry> ministryOptional = ministryService.findById(id);
        if (!ministryOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ministryOptional.get(), HttpStatus.OK);
    }

    @PostMapping("/ministries")
    public ResponseEntity<Ministry> createMinistry(@ModelAttribute MinistryForm ministryForm) {
        MultipartFile file = ministryForm.getAvatar();
        String fileName = file.getOriginalFilename();
        String fileUpload = environment.getProperty("upload.path").toString();
        String userName = ministryForm.getUserName();
        String password = ministryForm.getPassword();
        Set<AppRole> roleSet = ministryForm.getRoleSet();
        String fullName = ministryForm.getFullName();
        String phoneNumber = ministryForm.getPhoneNumber();
        String email = ministryForm.getEmail();
        Gender gender = ministryForm.getGender();
        String dateOfBirth = ministryForm.getDateOfBirth();
        String address = ministryForm.getAddress();
        try {
            FileCopyUtils.copy(ministryForm.getAvatar().getBytes(), new File(fileUpload + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        AppUser appUser = new AppUser(userName, password, roleSet);
        appUserService.save(appUser);
        Ministry ministry = new Ministry(appUser, fullName, phoneNumber, fileName, email, gender, dateOfBirth, address);
        ministryService.save(ministry);
        return new ResponseEntity<>(ministry, HttpStatus.CREATED);
    }

    @DeleteMapping("/ministries/{id}")
    public ResponseEntity<Ministry> deleteMinistry(@PathVariable Long id) {
        Optional<Ministry> ministryOptional = ministryService.findById(id);
        if (!ministryOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ministryService.remove(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/ministries/edit/{id}")
    public ResponseEntity<Ministry> editMinistry(@PathVariable Long id, @ModelAttribute MinistryForm ministryForm) {
        Optional<Ministry> ministryOptional = ministryService.findById(id);
        if (!ministryOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            MultipartFile file = ministryForm.getAvatar();
            String fileName = file.getOriginalFilename();
            String fileUpload = environment.getProperty("upload.path").toString();
            String userName = ministryForm.getUserName();
            String password = ministryForm.getPassword();
            Set<AppRole> roleSet = ministryForm.getRoleSet();
            String fullName = ministryForm.getFullName();
            String phoneNumber = ministryForm.getPhoneNumber();
            String email = ministryForm.getEmail();
            Gender gender = ministryForm.getGender();
            String dateOfBirth = ministryForm.getDateOfBirth();
            String address = ministryForm.getAddress();
            try {
                FileCopyUtils.copy(ministryForm.getAvatar().getBytes(), new File(fileUpload + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            AppUser appUser = new AppUser(userName, password, roleSet);
            appUserService.save(appUser);
            Ministry ministry = new Ministry(appUser, fullName, phoneNumber, fileName, email, gender, dateOfBirth, address);
            ministry.setId(id);
            ministryService.save(ministry);
            return new ResponseEntity<>(ministry, HttpStatus.CREATED);
        }
    }
}
