package ua.kiev.prog;


import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ContactService {
    private final ContactRepository contactRepository;
    private final GroupRepository groupRepository;

    public ContactService(ContactRepository contactRepository,
                          GroupRepository groupRepository) {
        this.contactRepository = contactRepository;
        this.groupRepository = groupRepository;
    }

    @Transactional
    public void addContact(Contact contact) {
        contactRepository.save(contact);
    }

    @Transactional
    public void addGroup(Group group) {
        groupRepository.save(group);
    }


    @Transactional
    public void deleteContacts(long[] idList) {
        for (long id : idList)
            contactRepository.deleteById(id);
    }

    @Transactional(readOnly=true)
    public List<Group> findGroups() {
        return groupRepository.findAll();
    }

    @Transactional(readOnly=true)
    public List<Contact> findAll(Pageable pageable) {
        return contactRepository.findAll(pageable).getContent();
    }
    @Transactional(readOnly = true)
    public List<Contact> findContactsByIds(long[] contactIds) {
        List<Long> contactIdList = Arrays.stream(contactIds).boxed().collect(Collectors.toList());
        return contactRepository.findAllById(contactIdList);
    }
    @Transactional(readOnly = true)
    public String exportSelectedContactsToCSV(long[] contactIds) throws IOException {
        List<Contact> selectedContacts = findContactsByIds(contactIds);

        StringBuilder csvContent = new StringBuilder();
        csvContent.append("Name,Surname,Phone,Email,Group\n");

        for (Contact contact : selectedContacts) {
            csvContent.append(
                    contact.getName() + "," +
                            contact.getSurname() + "," +
                            contact.getPhone() + "," +
                            contact.getEmail() + "," +
                            (contact.getGroup() != null ? contact.getGroup().getName() : "") + "\n"
            );
        }

        return csvContent.toString();
    }

    @Transactional(readOnly = true)
    public List<Contact> findAllContacts() {
        return contactRepository.findAll();
    }

    @Transactional(readOnly=true)
    public List<Contact> findByGroup(Group group, Pageable pageable) {
        return contactRepository.findByGroup(group, pageable);
    }

    @Transactional(readOnly = true)
    public long countByGroup(Group group) {
        return contactRepository.countByGroup(group);
    }

    @Transactional(readOnly=true)
    public List<Contact> findByPattern(String pattern, Pageable pageable) {
        return contactRepository.findByPattern(pattern, pageable);
    }

    @Transactional(readOnly = true)
    public long count() {
        return contactRepository.count();
    }

    @Transactional(readOnly=true)
    public Group findGroup(long id) {
        return groupRepository.findById(id).get();
    }

    @Transactional
    public void reset() {
        groupRepository.deleteAll();
        contactRepository.deleteAll();

        Group group = new Group("Test");
        Contact contact;

        addGroup(group);

        for (int i = 0; i < 13; i++) {
            contact = new Contact(null, "Name" + i, "Surname" + i, "1234567" + i, "user" + i + "@test.com");
            addContact(contact);
        }
        for (int i = 0; i < 10; i++) {
            contact = new Contact(group, "Other" + i, "OtherSurname" + i, "7654321" + i, "user" + i + "@other.com");
            addContact(contact);
        }
    }

}
