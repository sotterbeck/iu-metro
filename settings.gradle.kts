rootProject.name = "iu-metro"
include(
    "entity",
    "usecase",
    "entrypoints:papermc",
    "dataproviders:postgres"
)
findProject(":entrypoints:papermc")
findProject(":dataproviders:postgres")
