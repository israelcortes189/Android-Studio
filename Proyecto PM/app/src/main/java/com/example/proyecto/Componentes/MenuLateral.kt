package com.example.proyecto.Componentes

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.proyecto.Models.menu_Lateral
import com.example.proyecto.Models.menu_Lateral.menu_Lateral1
import com.example.proyecto.Navegacion.currentRoute

@Composable
fun MenuLateral(
    navController: NavHostController,
    drawerState: DrawerState,
    contenido: @Composable () -> Unit
){
    val scope = rememberCoroutineScope()
    val menu_items = listOf(
        menu_Lateral1,
        menu_Lateral.menu_Lateral2,
        menu_Lateral.menu_Lateral3,
        menu_Lateral.menu_Lateral4,
        menu_Lateral.menu_Lateral5
    )
    ModalNavigationDrawer(
        drawerState= drawerState,
        drawerContent = {
            ModalDrawerSheet {
                menu_items.forEach() {item->
                    NavigationDrawerItem(
                        modifier= Modifier.padding(10.dp),
                        icon={
                          Icon(item.icon,null)
                        },
                        label = {Text(text= stringResource(id = item.title))},
                        selected = currentRoute(navController) == item.ruta,
                        onClick = {

                            navController.navigate(item.ruta)
                        }
                    )
                }
            }
        }
    ) {
        contenido()
    }
}