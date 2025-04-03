rootProject.name = "iu-metro"
include(
    "domain",
    "application",
    "infrastructure:papermc",
    "infrastructure:postgres",
)
findProject(":infrastructure:papermc")
findProject(":infrastructure:postgres")
