rootProject.name = "iu-metro"
include(
    "entity",
    "usecase",
    "entry",
    "entrypoints:papermc",
    "dataproviders:jpa"
)
findProject(":entrypoints:papermc")?.name = "papermc"
findProject(":dataproviders:jpa")?.name = "jpa"
