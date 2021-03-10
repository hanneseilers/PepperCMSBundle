package de.fhkiel.pepper.cms_lib.repository.interfaces;

import de.fhkiel.pepper.cms_lib.repository.PepperCMSRepository;

public interface PepperCMSRepositoryLoadedCallable {
    void repositoryLoaded(PepperCMSRepository repository);
}
