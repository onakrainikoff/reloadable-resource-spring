package ru.on8off.reloadable.resource.spring.multy_dataloaders;

import ru.on8off.reloadable.resource.loader.DataLoader;


interface TestDataLoader extends DataLoader<Long> {
    Long update();
}
